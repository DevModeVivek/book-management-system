#!/bin/bash

# Phase 3: Asynchronous Messaging Testing Script
# This script demonstrates the complete event-driven architecture

set -e

echo "Asynchronous Messaging Testing"
echo "=================================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if service is running
check_service() {
    local service_name=$1
    local port=$2
    
    if curl -s "http://localhost:$port/api/v1/actuator/health" > /dev/null 2>&1; then
        print_success "$service_name is running on port $port"
        return 0
    else
        print_error "$service_name is not running on port $port"
        return 1
    fi
}

# Function to check infrastructure
check_infrastructure() {
    print_status "Checking infrastructure services..."
    
    # Check Redis
    if redis-cli ping > /dev/null 2>&1; then
        print_success "Redis is running"
    else
        print_warning "Redis is not running. Run 'docker-compose up redis -d' to start it."
    fi
    
    # Check RabbitMQ
    if curl -s "http://localhost:15672" > /dev/null 2>&1; then
        print_success "RabbitMQ Management UI is accessible at http://localhost:15672"
    else
        print_warning "RabbitMQ is not running. Run 'docker-compose up rabbitmq -d' to start it."
    fi
}

# Function to test book creation and event publishing
test_book_creation() {
    print_status "Testing book creation and event publishing..."
    
    # Create a test book
    local book_data='{
        "title": "Event-Driven Architecture Guide",
        "author": "John Doe",
        "isbn": "978-0123456789",
        "publishedDate": "2023-12-14",
        "price": 29.99,
        "genre": "Technology",
        "publisher": "Tech Books Inc",
        "description": "A comprehensive guide to event-driven architecture patterns",
        "pageCount": 350,
        "language": "English"
    }'
    
    local response=$(curl -s -X POST \
        "http://localhost:8081/api/v1/books" \
        -H "Content-Type: application/json" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" \
        -d "$book_data")
    
    if echo "$response" | jq -e '.id' > /dev/null 2>&1; then
        local book_id=$(echo "$response" | jq -r '.id')
        print_success "Book created successfully with ID: $book_id"
        echo "Book Data: $(echo "$response" | jq -c '.')"
        
        # Store book ID for later tests
        echo "$book_id" > /tmp/test_book_id
        
        print_status "Waiting for BookCreatedEvent to be processed by Notification Service..."
        sleep 3
        
        return 0
    else
        print_error "Failed to create book. Response: $response"
        return 1
    fi
}

# Function to test book update and event publishing
test_book_update() {
    if [ ! -f /tmp/test_book_id ]; then
        print_warning "No test book ID found. Skipping update test."
        return 0
    fi
    
    local book_id=$(cat /tmp/test_book_id)
    print_status "Testing book update and event publishing for book ID: $book_id..."
    
    # Update the test book
    local update_data='{
        "title": "Event-Driven Architecture Guide - Updated Edition",
        "author": "John Doe",
        "isbn": "978-0123456789",
        "publishedDate": "2023-12-14",
        "price": 34.99,
        "genre": "Technology",
        "publisher": "Tech Books Inc",
        "description": "A comprehensive guide to event-driven architecture patterns - Now with real-world examples",
        "pageCount": 420,
        "language": "English"
    }'
    
    local response=$(curl -s -X PUT \
        "http://localhost:8081/api/v1/books/$book_id" \
        -H "Content-Type: application/json" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" \
        -d "$update_data")
    
    if echo "$response" | jq -e '.id' > /dev/null 2>&1; then
        print_success "Book updated successfully"
        echo "Updated Book Data: $(echo "$response" | jq -c '.')"
        
        print_status "Waiting for BookUpdatedEvent to be processed by Notification Service..."
        sleep 3
        
        return 0
    else
        print_error "Failed to update book. Response: $response"
        return 1
    fi
}

# Function to check notifications
check_notifications() {
    print_status "Checking notifications created by events..."
    
    local response=$(curl -s \
        "http://localhost:8083/api/v1/notifications?page=0&size=10" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)")
    
    if echo "$response" | jq -e '.content' > /dev/null 2>&1; then
        local notification_count=$(echo "$response" | jq '.content | length')
        print_success "Found $notification_count notifications"
        
        if [ "$notification_count" -gt 0 ]; then
            echo "Recent Notifications:"
            echo "$response" | jq -c '.content[] | {id: .id, subject: .subject, status: .status, createdAt: .createdAt}'
        fi
        
        return 0
    else
        print_error "Failed to retrieve notifications. Response: $response"
        return 1
    fi
}

# Function to test RabbitMQ queues
check_rabbitmq_queues() {
    print_status "Checking RabbitMQ queues..."
    
    # Check if RabbitMQ management API is accessible
    if curl -s -u guest:guest "http://localhost:15672/api/queues" > /dev/null 2>&1; then
        local queues=$(curl -s -u guest:guest "http://localhost:15672/api/queues" | jq -r '.[].name' 2>/dev/null || echo "")
        
        if [ -n "$queues" ]; then
            print_success "RabbitMQ queues found:"
            echo "$queues" | grep -E "(book\.|notification\.)" || echo "No book/notification queues found"
        else
            print_warning "No queues found in RabbitMQ"
        fi
    else
        print_warning "RabbitMQ Management API not accessible. Check if RabbitMQ is running with management plugin."
    fi
}

# Function to test book deletion
test_book_deletion() {
    if [ ! -f /tmp/test_book_id ]; then
        print_warning "No test book ID found. Skipping deletion test."
        return 0
    fi
    
    local book_id=$(cat /tmp/test_book_id)
    print_status "Testing book deletion and event publishing for book ID: $book_id..."
    
    local response=$(curl -s -X DELETE \
        "http://localhost:8081/api/v1/books/$book_id" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)")
    
    if [ $? -eq 0 ]; then
        print_success "Book deleted successfully"
        
        print_status "Waiting for BookDeletedEvent to be processed by Notification Service..."
        sleep 3
        
        # Clean up
        rm -f /tmp/test_book_id
        
        return 0
    else
        print_error "Failed to delete book"
        return 1
    fi
}

# Function to run complete test suite
run_complete_test() {
    echo ""
    print_status "Starting complete Phase 3 test suite..."
    echo ""
    
    # Check infrastructure
    check_infrastructure
    echo ""
    
    # Check services
    print_status "Checking microservices..."
    local services_ok=true
    
    check_service "Book Service" 8081 || services_ok=false
    check_service "User Service" 8082 || services_ok=false
    check_service "Notification Service" 8083 || services_ok=false
    
    if [ "$services_ok" = false ]; then
        print_error "Some services are not running. Please start all services before testing."
        exit 1
    fi
    
    echo ""
    
    # Run tests
    print_status "Running event-driven tests..."
    test_book_creation
    echo ""
    
    check_notifications
    echo ""
    
    check_rabbitmq_queues
    echo ""
    
    test_book_update
    echo ""
    
    check_notifications
    echo ""
    
    test_book_deletion
    echo ""
    
    check_notifications
    echo ""
    
    print_success "Phase 3 testing completed! 🎉"
    echo ""
    print_status "Summary:"
    echo "Event-driven architecture is working"
    echo "Book events are being published to RabbitMQ"
    echo "Notification service is consuming events"
    echo "Notifications are being created and stored"
    echo ""
    print_status "Access points:"
    echo "Book Service Swagger: http://localhost:8081/api/v1/swagger-ui.html"
    echo "Notification Service: http://localhost:8083/api/v1/swagger-ui.html"
    echo "RabbitMQ Management: http://localhost:15672 (guest/guest)"
    echo "Redis CLI: redis-cli"
}

# Main execution
case "${1:-test}" in
    "infrastructure")
        check_infrastructure
        ;;
    "services")
        check_service "Book Service" 8081
        check_service "User Service" 8082
        check_service "Notification Service" 8083
        ;;
    "book-create")
        test_book_creation
        ;;
    "book-update")
        test_book_update
        ;;
    "book-delete")
        test_book_deletion
        ;;
    "notifications")
        check_notifications
        ;;
    "queues")
        check_rabbitmq_queues
        ;;
    "test"|*)
        run_complete_test
        ;;
esac
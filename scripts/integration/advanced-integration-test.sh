#!/bin/bash

# Advanced System Integration Testing Script
# Tests the complete event-driven workflow with performance monitoring

set -e

echo "Advanced Integration Testing - Book Management System"
echo "======================================================="

# Test configuration
BOOK_SERVICE_URL="http://localhost:8081/api/v1"
USER_SERVICE_URL="http://localhost:8082/api/v1"
NOTIFICATION_SERVICE_URL="http://localhost:8083/api/v1"
RABBITMQ_MGMT_URL="http://localhost:15672/api"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }
print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }

# Function to test service health
test_service_health() {
    local service_name=$1
    local service_url=$2
    
    print_info "Testing $service_name health..."
    
    response=$(curl -s -w "%{http_code}" -o /tmp/health_response.json "$service_url/actuator/health" 2>/dev/null || echo "000")
    
    if [ "$response" = "200" ]; then
        status=$(cat /tmp/health_response.json | jq -r '.status' 2>/dev/null || echo "UNKNOWN")
        if [ "$status" = "UP" ]; then
            print_success "$service_name is healthy"
            return 0
        else
            print_warning "$service_name reports status: $status"
            return 1
        fi
    else
        print_error "$service_name is not responding (HTTP: $response)"
        return 1
    fi
}

# Function to test complete CRUD workflow with events
test_complete_crud_workflow() {
    print_info "Starting complete CRUD workflow test..."
    
    # Test data
    local test_book='{
        "title": "Advanced Microservices Architecture",
        "author": "System Test",
        "isbn": "978-0987654321",
        "publishedDate": "2025-12-14",
        "price": 45.99,
        "genre": "Technology",
        "publisher": "Test Publishers",
        "description": "Comprehensive guide to building scalable microservices",
        "pageCount": 450,
        "language": "English"
    }'
    
    # CREATE: Test book creation
    print_info "1. Testing book creation..."
    create_response=$(curl -s -X POST "$BOOK_SERVICE_URL/books" \
        -H "Content-Type: application/json" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" \
        -d "$test_book" \
        -w "%{http_code}" -o /tmp/create_response.json 2>/dev/null)
    
    if [ "${create_response: -3}" = "201" ] || [ "${create_response: -3}" = "200" ]; then
        book_id=$(cat /tmp/create_response.json | jq -r '.id // .data.id' 2>/dev/null)
        if [ "$book_id" != "null" ] && [ -n "$book_id" ]; then
            print_success "Book created successfully with ID: $book_id"
            echo "$book_id" > /tmp/test_book_id
        else
            print_error "Book creation response invalid"
            return 1
        fi
    else
        print_error "Book creation failed (HTTP: ${create_response: -3})"
        return 1
    fi
    
    # Wait for event processing
    print_info "Waiting for event processing..."
    sleep 3
    
    # READ: Test book retrieval
    print_info "2. Testing book retrieval..."
    read_response=$(curl -s -w "%{http_code}" -o /tmp/read_response.json \
        "$BOOK_SERVICE_URL/books/$book_id" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" 2>/dev/null)
    
    if [ "${read_response: -3}" = "200" ]; then
        print_success "Book retrieved successfully"
    else
        print_error "Book retrieval failed (HTTP: ${read_response: -3})"
    fi
    
    # UPDATE: Test book update
    print_info "3. Testing book update..."
    local updated_book=$(echo "$test_book" | jq '.price = 49.99 | .title = "Advanced Microservices Architecture - 2nd Edition"')
    
    update_response=$(curl -s -X PUT "$BOOK_SERVICE_URL/books/$book_id" \
        -H "Content-Type: application/json" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" \
        -d "$updated_book" \
        -w "%{http_code}" -o /tmp/update_response.json 2>/dev/null)
    
    if [ "${update_response: -3}" = "200" ]; then
        print_success "Book updated successfully"
    else
        print_error "Book update failed (HTTP: ${update_response: -3})"
    fi
    
    # Wait for event processing
    sleep 3
    
    # CHECK NOTIFICATIONS: Test notification creation
    print_info "4. Testing notification generation..."
    notif_response=$(curl -s -w "%{http_code}" -o /tmp/notif_response.json \
        "$NOTIFICATION_SERVICE_URL/notifications?page=0&size=10" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" 2>/dev/null)
    
    if [ "${notif_response: -3}" = "200" ]; then
        notif_count=$(cat /tmp/notif_response.json | jq '.content | length' 2>/dev/null || echo 0)
        print_success "Found $notif_count notifications in system"
        
        if [ "$notif_count" -gt 0 ]; then
            print_info "Recent notifications:"
            cat /tmp/notif_response.json | jq -r '.content[0:3][] | "  • " + .subject + " (" + .status + ")"' 2>/dev/null || echo "  • Could not parse notification details"
        fi
    else
        print_warning "Could not retrieve notifications (HTTP: ${notif_response: -3})"
    fi
    
    # DELETE: Test book deletion
    print_info "5. Testing book deletion..."
    delete_response=$(curl -s -X DELETE "$BOOK_SERVICE_URL/books/$book_id" \
        -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" \
        -w "%{http_code}" -o /dev/null 2>/dev/null)
    
    if [ "${delete_response: -3}" = "204" ] || [ "${delete_response: -3}" = "200" ]; then
        print_success "Book deleted successfully"
    else
        print_error "Book deletion failed (HTTP: ${delete_response: -3})"
    fi
    
    # Wait for final event processing
    sleep 3
    
    # Clean up
    rm -f /tmp/test_book_id
    print_success "Complete CRUD workflow test finished"
}

# Function to test RabbitMQ message flow
test_rabbitmq_message_flow() {
    print_info "Testing RabbitMQ message flow..."
    
    # Check if RabbitMQ management API is accessible
    rabbitmq_response=$(curl -s -u guest:guest -w "%{http_code}" -o /tmp/rabbitmq_overview.json \
        "$RABBITMQ_MGMT_URL/overview" 2>/dev/null || echo "000")
    
    if [ "${rabbitmq_response: -3}" = "200" ]; then
        print_success "RabbitMQ Management API accessible"
        
        # Get queue information
        queue_response=$(curl -s -u guest:guest -w "%{http_code}" -o /tmp/rabbitmq_queues.json \
            "$RABBITMQ_MGMT_URL/queues" 2>/dev/null || echo "000")
        
        if [ "${queue_response: -3}" = "200" ]; then
            queue_count=$(cat /tmp/rabbitmq_queues.json | jq '. | length' 2>/dev/null || echo 0)
            print_success "Found $queue_count queues in RabbitMQ"
            
            if [ "$queue_count" -gt 0 ]; then
                print_info "Queue statistics:"
                cat /tmp/rabbitmq_queues.json | jq -r '.[] | "  • " + .name + ": " + (.messages | tostring) + " messages"' 2>/dev/null || echo "  • Could not parse queue details"
            fi
        else
            print_warning "Could not retrieve queue information"
        fi
    else
        print_warning "RabbitMQ Management API not accessible"
    fi
}

# Function to test performance metrics
test_performance_metrics() {
    print_info "Testing performance metrics..."
    
    for service in "book-service" "user-service" "notification-service"; do
        case $service in
            "book-service") url=$BOOK_SERVICE_URL ;;
            "user-service") url=$USER_SERVICE_URL ;;
            "notification-service") url=$NOTIFICATION_SERVICE_URL ;;
        esac
        
        metrics_response=$(curl -s -w "%{http_code}" -o /tmp/metrics_response.json \
            "$url/actuator/metrics" 2>/dev/null || echo "000")
        
        if [ "${metrics_response: -3}" = "200" ]; then
            metric_count=$(cat /tmp/metrics_response.json | jq '.names | length' 2>/dev/null || echo 0)
            print_success "$service: $metric_count metrics available"
        else
            print_warning "$service: Metrics endpoint not accessible"
        fi
    done
}

# Function to generate performance report
generate_performance_report() {
    print_info "Generating performance report..."
    
    echo "Performance Test Results" > /tmp/performance_report.txt
    echo "===========================" >> /tmp/performance_report.txt
    echo "Generated: $(date)" >> /tmp/performance_report.txt
    echo "" >> /tmp/performance_report.txt
    
    # Test response times
    print_info "Testing response times..."
    
    for endpoint in "/actuator/health" "/actuator/info"; do
        for service_url in $BOOK_SERVICE_URL $USER_SERVICE_URL $NOTIFICATION_SERVICE_URL; do
            start_time=$(date +%s%N)
            response=$(curl -s -w "%{http_code}" -o /dev/null "$service_url$endpoint" 2>/dev/null || echo "000")
            end_time=$(date +%s%N)
            
            duration=$(( (end_time - start_time) / 1000000 )) # Convert to milliseconds
            
            service_name=$(echo $service_url | cut -d: -f3 | sed 's|/.*||')
            
            if [ "${response: -3}" = "200" ]; then
                echo "$service_name$endpoint: ${duration}ms (HTTP 200)" >> /tmp/performance_report.txt
            else
                echo "$service_name$endpoint: FAILED (HTTP ${response: -3})" >> /tmp/performance_report.txt
            fi
        done
    done
    
    print_success "Performance report generated: /tmp/performance_report.txt"
}

# Main test execution
main() {
    echo ""
    print_info "Starting Advanced Integration Testing..."
    echo ""
    
    # Test all services
    all_healthy=true
    test_service_health "Book Service" $BOOK_SERVICE_URL || all_healthy=false
    test_service_health "User Service" $USER_SERVICE_URL || all_healthy=false
    test_service_health "Notification Service" $NOTIFICATION_SERVICE_URL || all_healthy=false
    
    echo ""
    
    if [ "$all_healthy" = true ]; then
        print_success "All services are healthy, proceeding with integration tests..."
        echo ""
        
        # Run comprehensive tests
        test_complete_crud_workflow
        echo ""
        test_rabbitmq_message_flow
        echo ""
        test_performance_metrics
        echo ""
        generate_performance_report
        echo ""
        
        print_success "Advanced integration testing completed successfully!"
    else
        print_error "Some services are not healthy. Please start all services before running integration tests."
        exit 1
    fi
    
    echo ""
    print_info "Generated files:"
    echo "  • /tmp/performance_report.txt - Performance test results"
    echo "  • /tmp/*_response.json - API response details"
    echo ""
}

# Execute based on arguments
case "${1:-all}" in
    "health")
        test_service_health "Book Service" $BOOK_SERVICE_URL
        test_service_health "User Service" $USER_SERVICE_URL
        test_service_health "Notification Service" $NOTIFICATION_SERVICE_URL
        ;;
    "crud")
        test_complete_crud_workflow
        ;;
    "messaging")
        test_rabbitmq_message_flow
        ;;
    "performance")
        test_performance_metrics
        generate_performance_report
        ;;
    "all"|*)
        main
        ;;
esac
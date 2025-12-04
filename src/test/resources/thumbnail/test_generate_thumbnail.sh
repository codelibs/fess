#!/bin/bash
#
# Test script for generate-thumbnail
# This script tests the functionality of the generate-thumbnail script
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Find the project root by looking for pom.xml
PROJECT_ROOT="${SCRIPT_DIR}"
while [[ ! -f "${PROJECT_ROOT}/pom.xml" ]] && [[ "${PROJECT_ROOT}" != "/" ]]; do
    PROJECT_ROOT="$(dirname "${PROJECT_ROOT}")"
done
GENERATE_THUMBNAIL="${PROJECT_ROOT}/src/main/assemblies/files/generate-thumbnail"
TEST_DIR="${SCRIPT_DIR}"
OUTPUT_DIR="/tmp/thumbnail_test_$$"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0

# Create output directory
mkdir -p "${OUTPUT_DIR}"

# Cleanup function
cleanup() {
    rm -rf "${OUTPUT_DIR}"
}
trap cleanup EXIT

# Test function
run_test() {
    local test_name="$1"
    local expected_result="$2"
    shift 2
    local cmd=("$@")

    echo -n "Testing: ${test_name}... "

    # Run the command
    "${cmd[@]}" >/dev/null 2>&1
    local actual_result=$?

    if [[ "${actual_result}" -eq "${expected_result}" ]]; then
        echo -e "${GREEN}PASSED${NC}"
        ((TESTS_PASSED++))
        return 0
    else
        echo -e "${RED}FAILED${NC} (expected: ${expected_result}, got: ${actual_result})"
        ((TESTS_FAILED++))
        return 1
    fi
}

# Test function for checking file existence
run_file_test() {
    local test_name="$1"
    local output_file="$2"
    shift 2
    local cmd=("$@")

    echo -n "Testing: ${test_name}... "

    # Run the command
    "${cmd[@]}" >/dev/null 2>&1
    local result=$?

    if [[ "${result}" -eq 0 ]] && [[ -f "${output_file}" ]] && [[ -s "${output_file}" ]]; then
        echo -e "${GREEN}PASSED${NC}"
        ((TESTS_PASSED++))
        rm -f "${output_file}"
        return 0
    else
        echo -e "${RED}FAILED${NC} (exit: ${result}, file exists: $([[ -f "${output_file}" ]] && echo "yes" || echo "no"))"
        ((TESTS_FAILED++))
        rm -f "${output_file}"
        return 1
    fi
}

echo "=========================================="
echo "Testing generate-thumbnail script"
echo "=========================================="
echo ""

# Check if the script exists
if [[ ! -f "${GENERATE_THUMBNAIL}" ]]; then
    echo -e "${RED}ERROR: generate-thumbnail script not found at ${GENERATE_THUMBNAIL}${NC}"
    exit 1
fi

# Make sure the script is executable
chmod +x "${GENERATE_THUMBNAIL}"

echo "--- Basic argument tests ---"

# Test: No arguments
run_test "no arguments" 1 "${GENERATE_THUMBNAIL}"

# Test: Empty command type
run_test "empty command type" 1 "${GENERATE_THUMBNAIL}" "" "file:/tmp/test" "/tmp/out.png"

# Test: Unsupported command type
run_test "unsupported command type" 1 "${GENERATE_THUMBNAIL}" "unsupported" "file:/tmp/test" "/tmp/out.png"

echo ""
echo "--- get_imagemagick_cmd function tests ---"

# Test that ImageMagick is available (either convert or magick)
if command -v magick >/dev/null 2>&1 || command -v convert >/dev/null 2>&1; then
    echo -e "ImageMagick available: ${GREEN}YES${NC}"

    echo ""
    echo "--- Image thumbnail tests (requires ImageMagick) ---"

    # Test: PNG thumbnail generation
    if [[ -f "${TEST_DIR}/400x400.png" ]]; then
        run_file_test "PNG thumbnail" "${OUTPUT_DIR}/png_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/400x400.png" "${OUTPUT_DIR}/png_thumb.png" "image/png"
    fi

    # Test: JPEG thumbnail generation
    if [[ -f "${TEST_DIR}/400x400.jpg" ]]; then
        run_file_test "JPEG thumbnail" "${OUTPUT_DIR}/jpg_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/400x400.jpg" "${OUTPUT_DIR}/jpg_thumb.png" "image/jpeg"
    fi

    # Test: GIF thumbnail generation
    if [[ -f "${TEST_DIR}/400x400.gif" ]]; then
        run_file_test "GIF thumbnail" "${OUTPUT_DIR}/gif_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/400x400.gif" "${OUTPUT_DIR}/gif_thumb.png" "image/gif"
    fi

    # Test: Image without mimetype (backward compatibility)
    if [[ -f "${TEST_DIR}/400x400.png" ]]; then
        run_file_test "PNG without mimetype" "${OUTPUT_DIR}/nomime_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/400x400.png" "${OUTPUT_DIR}/nomime_thumb.png"
    fi

    # Test: Different aspect ratios
    if [[ -f "${TEST_DIR}/400x600.jpg" ]]; then
        run_file_test "Portrait JPEG (400x600)" "${OUTPUT_DIR}/portrait_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/400x600.jpg" "${OUTPUT_DIR}/portrait_thumb.png" "image/jpeg"
    fi

    if [[ -f "${TEST_DIR}/600x400.jpg" ]]; then
        run_file_test "Landscape JPEG (600x400)" "${OUTPUT_DIR}/landscape_thumb.png" \
            "${GENERATE_THUMBNAIL}" "image" "file:${TEST_DIR}/600x400.jpg" "${OUTPUT_DIR}/landscape_thumb.png" "image/jpeg"
    fi
else
    echo -e "ImageMagick available: ${RED}NO${NC} (skipping image tests)"
fi

echo ""
echo "--- SVG thumbnail tests (requires rsvg-convert) ---"

if command -v rsvg-convert >/dev/null 2>&1; then
    echo -e "rsvg-convert available: ${GREEN}YES${NC}"

    # Create a simple SVG test file
    SVG_TEST="${OUTPUT_DIR}/test.svg"
    cat > "${SVG_TEST}" << 'EOF'
<svg width="200" height="200" xmlns="http://www.w3.org/2000/svg">
  <rect width="200" height="200" fill="blue"/>
  <circle cx="100" cy="100" r="80" fill="red"/>
</svg>
EOF

    # Test: SVG thumbnail generation
    run_file_test "SVG thumbnail" "${OUTPUT_DIR}/svg_thumb.png" \
        "${GENERATE_THUMBNAIL}" "svg" "file:${SVG_TEST}" "${OUTPUT_DIR}/svg_thumb.png"

    rm -f "${SVG_TEST}"
else
    echo -e "rsvg-convert available: ${RED}NO${NC} (skipping SVG tests)"
fi

echo ""
echo "--- Format hint tests ---"

# Test that format hints are properly constructed
# This is a logical test of the script structure
echo -n "Testing: format hint construction... "
if grep -q 'format_hint=""' "${GENERATE_THUMBNAIL}" && \
   grep -q 'image/gif.*gif:' "${GENERATE_THUMBNAIL}" && \
   grep -q 'image/tiff.*tiff:' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "--- ImageMagick version detection tests ---"

echo -n "Testing: get_imagemagick_cmd function exists... "
if grep -q 'get_imagemagick_cmd()' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo -n "Testing: magick command check in get_imagemagick_cmd... "
if grep -q 'command -v magick' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo -n "Testing: convert command fallback... "
if grep -q 'command -v convert' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "--- Mimetype argument tests ---"

echo -n "Testing: mimetype is 4th argument... "
if grep -q 'mimetype=\${4:-}' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "--- Platform compatibility tests ---"

echo -n "Testing: Linux root HOME check... "
if grep -q 'x"$HOME" = "x/root"' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo -n "Testing: Mac root HOME check... "
if grep -q 'x"$HOME" = "x/var/root"' "${GENERATE_THUMBNAIL}"; then
    echo -e "${GREEN}PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo -e "Passed: ${GREEN}${TESTS_PASSED}${NC}"
echo -e "Failed: ${RED}${TESTS_FAILED}${NC}"
echo ""

if [[ ${TESTS_FAILED} -eq 0 ]]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi

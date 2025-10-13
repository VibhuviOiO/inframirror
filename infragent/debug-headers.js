// Test to see what headers axios sends vs what the API expects
import axios from 'axios';

const testUrl = 'https://recs.richrelevance.com/rrserver/api/rrPlatform/getProducts?apiClientKey=57c9ee3598e629df&apiKey=24f07d816ef94d7f&productId=582672113-COR582672113-19-4007TCX';

console.log('Testing different header configurations...\n');

// Test 1: Default axios request (what our agent currently sends)
console.log('1. Default axios request:');
try {
  const response1 = await axios.get(testUrl, {
    timeout: 10000,
    validateStatus: () => true
  });
  console.log(`   Status: ${response1.status}`);
  console.log(`   Headers sent: ${JSON.stringify(response1.config.headers, null, 2)}`);
} catch (error) {
  console.log(`   Error: ${error.message}`);
}

console.log('\n2. With User-Agent matching curl:');
try {
  const response2 = await axios.get(testUrl, {
    timeout: 10000,
    validateStatus: () => true,
    headers: {
      'User-Agent': 'curl/8.7.1'
    }
  });
  console.log(`   Status: ${response2.status}`);
} catch (error) {
  console.log(`   Error: ${error.message}`);
}

console.log('\n3. With Accept header matching curl:');
try {
  const response3 = await axios.get(testUrl, {
    timeout: 10000,
    validateStatus: () => true,
    headers: {
      'User-Agent': 'curl/8.7.1',
      'Accept': '*/*'
    }
  });
  console.log(`   Status: ${response3.status}`);
} catch (error) {
  console.log(`   Error: ${error.message}`);
}

console.log('\n4. With browser-like User-Agent:');
try {
  const response4 = await axios.get(testUrl, {
    timeout: 10000,
    validateStatus: () => true,
    headers: {
      'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
    }
  });
  console.log(`   Status: ${response4.status}`);
} catch (error) {
  console.log(`   Error: ${error.message}`);
}
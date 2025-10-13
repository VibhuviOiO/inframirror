// Simple test to verify detailed HTTP timing collection
import { HttpCollector } from './dist/collectors/http-collector.js';

const testConfig = {
  enabled: true,
  targets: [
    {
      name: 'HTTPS Test',
      url: 'https://www.google.com',
      timeout_seconds: 10
    },
    {
      name: 'HTTP Test',
      url: 'http://httpbin.org/get',
      timeout_seconds: 10
    }
  ]
};

async function testTimingCollection() {
  console.log('Testing detailed HTTP timing collection...');
  
  const collector = new HttpCollector(testConfig);
  const results = await collector.collect();
  
  if (results.length > 0) {
    results.forEach((result, index) => {
      console.log(`\n${result.targetName} (${result.monitorType}):`);
      console.log('- DNS Lookup:', result.dnsLookupMs, 'ms');
      console.log('- TCP Connect:', result.tcpConnectMs, 'ms');
      console.log('- TLS Handshake:', result.tlsHandshakeMs || 'N/A', 'ms');
      console.log('- Time to First Byte:', result.timeToFirstByteMs, 'ms');
      console.log('- Status:', result.responseStatusCode);
      console.log('- Success:', result.success);
    });
  } else {
    console.log('No results returned');
  }
}

testTimingCollection().catch(console.error);
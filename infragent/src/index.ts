// Main entry point for Infragent monitoring agent
import { Infragent } from './agent/infragent.js';
import { Logger } from './utils/logger.js';

const logger = Logger.getInstance();

async function main() {
  try {
    logger.info('🚀 Starting Infragent monitoring agent...');
    
    const agent = new Infragent();
    await agent.start();
    
    // Graceful shutdown handling
    const shutdownHandler = async (signal: string) => {
      logger.info(`📡 Received ${signal}, initiating graceful shutdown...`);
      await agent.stop();
      logger.info('✅ Infragent agent stopped gracefully');
      process.exit(0);
    };
    
    process.on('SIGINT', () => shutdownHandler('SIGINT'));
    process.on('SIGTERM', () => shutdownHandler('SIGTERM'));
    process.on('SIGUSR2', () => shutdownHandler('SIGUSR2')); // Nodemon
    
    logger.info('✅ Infragent started successfully');
    
  } catch (error) {
    logger.error('❌ Failed to start Infragent', { error: error instanceof Error ? error.message : String(error) });
    process.exit(1);
  }
}

// Handle uncaught exceptions
process.on('uncaughtException', (error) => {
  logger.error('💥 Uncaught exception', { error: error.message, stack: error.stack });
  process.exit(1);
});

process.on('unhandledRejection', (reason, promise) => {
  logger.error('💥 Unhandled rejection', { reason, promise });
  process.exit(1);
});

main();
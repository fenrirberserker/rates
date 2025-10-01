const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const cors = require('cors');
const { Kafka } = require('kafkajs');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: "http://localhost:3000", // React app URL
    methods: ["GET", "POST"]
  }
});

// Middleware
app.use(cors());
app.use(express.json());

// Kafka configuration
const kafkaConfig = {
  clientId: 'stock-visualizer-consumer',
  brokers: process.env.KAFKA_BROKERS ? process.env.KAFKA_BROKERS.split(',') : ['localhost:9092']
};

const kafka_client = new Kafka(kafkaConfig);
const consumer = kafka_client.consumer({ groupId: 'stock-visualizer-group' });

// Stock data simulation - enabled by default for demo
let simulateData = process.env.SIMULATE_DATA !== 'false';
let simulationInterval;

// Demo mode - always start with simulation
const demoMode = process.env.DEMO_MODE !== 'false';

// Function to simulate stock data with random values 1-100
const simulateStockData = () => {
  // Generate random values between 1-100
  const high = Math.floor(Math.random() * 100) + 1;
  const low = Math.floor(Math.random() * 100) + 1;
  
  // Ensure High is always greater than or equal to Low
  const finalHigh = Math.max(high, low);
  const finalLow = Math.min(high, low);
  
  return {
    High: finalHigh,
    Low: finalLow
  };
};

// Function to start data simulation
const startSimulation = () => {
  console.log('Starting stock data simulation with random values 1-100...');
  simulationInterval = setInterval(() => {
    const stockData = simulateStockData();
    console.log('Simulated data:', stockData);
    io.emit('stockData', stockData);
  }, 1000); // Send data every 1 second for demo
};

// Function to stop data simulation
const stopSimulation = () => {
  if (simulationInterval) {
    clearInterval(simulationInterval);
    simulationInterval = null;
    console.log('Stock data simulation stopped');
  }
};

// Kafka consumer setup
const setupKafkaConsumer = async () => {
  try {
    await consumer.connect();
    console.log('Connected to Kafka');
    
    // Subscribe to the stock data topic
    await consumer.subscribe({ 
      topic: process.env.KAFKA_TOPIC || 'stock-data'
    });

    // Process messages
    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        try {
          const stockData = JSON.parse(message.value.toString());
          console.log('Received from Kafka:', stockData);
          
          // Validate data structure
          if (stockData && typeof stockData.High === 'number' && typeof stockData.Low === 'number') {
            io.emit('stockData', stockData);
          } else {
            console.error('Invalid stock data format:', stockData);
          }
        } catch (error) {
          console.error('Error processing Kafka message:', error);
        }
      },
    });
    
    // If Kafka is working, stop simulation
    if (simulateData) {
      stopSimulation();
      simulateData = false;
      console.log('Kafka consumer active, simulation stopped');
    }
  } catch (error) {
    console.error('Error setting up Kafka consumer:', error);
    console.log('Falling back to data simulation');
    
    if (!simulateData) {
      simulateData = true;
      startSimulation();
    }
  }
};

// Socket.io connection handling
io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);
  
  // Send welcome message
  socket.emit('message', 'Connected to Stock Data Server');
  
  socket.on('disconnect', () => {
    console.log('Client disconnected:', socket.id);
  });
  
  socket.on('error', (error) => {
    console.error('Socket error:', error);
  });
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
    demo_mode: demoMode,
    kafka_connected: !simulateData && !demoMode,
    simulating: simulateData,
    connected_clients: io.engine.clientsCount,
    data_interval: demoMode ? '1 second' : '2 seconds',
    value_range: demoMode ? '1-100' : 'variable'
  });
});

// API endpoint to get current configuration
app.get('/config', (req, res) => {
  res.json({
    kafka_brokers: kafkaConfig.brokers,
    kafka_topic: process.env.KAFKA_TOPIC || 'stock-data',
    simulate_data: simulateData,
    server_port: PORT
  });
});

// Graceful shutdown
const gracefulShutdown = async () => {
  console.log('Shutting down gracefully...');
  
  stopSimulation();
  
  try {
    await consumer.disconnect();
    console.log('Kafka consumer disconnected');
  } catch (error) {
    console.error('Error disconnecting from Kafka:', error);
  }
  
  server.close(() => {
    console.log('Server closed');
    process.exit(0);
  });
};

process.on('SIGTERM', gracefulShutdown);
process.on('SIGINT', gracefulShutdown);

// Start server
const PORT = process.env.PORT || 3001;

server.listen(PORT, () => {
  console.log(`Stock data server running on port ${PORT}`);
  console.log(`Health check available at: http://localhost:${PORT}/health`);
  console.log(`Configuration at: http://localhost:${PORT}/config`);
  console.log(`Demo mode: ${demoMode ? 'ENABLED' : 'DISABLED'}`);
  
  // Always start simulation immediately in demo mode
  if (demoMode) {
    console.log('=== DEMO MODE ACTIVE ===');
    console.log('Generating random stock data (1-100) every second');
    startSimulation();
  } else {
    // Original behavior for production
    if (simulateData) {
      startSimulation();
    }
    // Try to setup Kafka consumer
    setupKafkaConsumer();
  }
});

module.exports = { app, server };
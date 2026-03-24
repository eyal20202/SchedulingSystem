// API Base URL from environment or default
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Exported as constant for use across the app
export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  TIMEOUT: 10000,
};

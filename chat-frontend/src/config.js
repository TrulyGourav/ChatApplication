const config = {
  BASE_URL: process.env.REACT_APP_BASE_URL,
  JOIN_TYPES: process.env.REACT_APP_JOIN_TYPES?.split(',') || ['CREATE', 'RANDOM', 'SPECIFIC'],
};

export default config;

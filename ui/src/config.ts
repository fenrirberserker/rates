export const config = {
  sseUrl:             process.env.REACT_APP_SSE_URL          ?? 'http://localhost:8080/api/trading/events',
  sseEventType:       process.env.REACT_APP_SSE_EVENT_TYPE   ?? 'stock-update',
  maxPointsPerSymbol: Number(process.env.REACT_APP_MAX_POINTS_PER_SYMBOL ?? 200),
};
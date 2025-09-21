import React, { useState, useEffect } from 'react';
import './App.css';
import ChartCanvas from './components/ChartCanvas';

function App() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('/sample-bullish1.json');
      if (!response.ok) {
        throw new Error('Failed to fetch sample-bullish1.json');
      }
      const jsonData = await response.json();
      setData(jsonData);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>Trading Data Visualization</h1>

        {loading && <p>Loading data...</p>}
        {error && <p style={{color: 'red'}}>Error: {error}</p>}
        {!loading && !error && data.length > 0 && <ChartCanvas data={data} />}
      </header>
    </div>
  );
}

export default App;

import React, { useRef, useEffect } from 'react';

const ChartCanvas = ({ data }) => {
  const canvasRef = useRef(null);

  useEffect(() => {
    if (!data || data.length === 0) return;

    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;
    const margin = 50;
    const chartWidth = width - margin * 2;
    const chartHeight = height - margin * 2;

    // Clear canvas
    ctx.clearRect(0, 0, width, height);

    // Y-axis scale (1-100)
    const minY = 1;
    const maxY = 100;
    const yRange = maxY - minY;

    // Calculate positions
    const xStep = chartWidth / (data.length - 1);
    
    // Draw Y-axis grid and labels
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    ctx.fillStyle = '#000';
    ctx.font = '10px Arial';
    
    for (let i = 0; i <= 10; i++) {
      const y = margin + (chartHeight / 10) * i;
      const value = maxY - (i * 10);
      
      // Grid line
      ctx.beginPath();
      ctx.moveTo(margin, y);
      ctx.lineTo(width - margin, y);
      ctx.stroke();
      
      // Y-axis label
      ctx.fillText(value.toString(), 10, y + 3);
    }

    // Draw X-axis grid and labels
    for (let i = 0; i < data.length; i++) {
      const x = margin + i * xStep;
      
      // Grid line
      ctx.beginPath();
      ctx.moveTo(x, margin);
      ctx.lineTo(x, height - margin);
      ctx.stroke();
      
      // X-axis label
      ctx.fillText((i + 1).toString(), x - 5, height - 10);
    }

    // Draw High line
    ctx.strokeStyle = '#ff4444';
    ctx.lineWidth = 2;
    ctx.beginPath();
    data.forEach((item, index) => {
      const x = margin + index * xStep;
      const y = margin + chartHeight - ((item.High - minY) / yRange) * chartHeight;
      if (index === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });
    ctx.stroke();

    // Draw Low line
    ctx.strokeStyle = '#4444ff';
    ctx.lineWidth = 2;
    ctx.beginPath();
    data.forEach((item, index) => {
      const x = margin + index * xStep;
      const y = margin + chartHeight - ((item.Low - minY) / yRange) * chartHeight;
      if (index === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });
    ctx.stroke();

    // Draw legend
    ctx.fillStyle = '#ff4444';
    ctx.fillRect(width - 150, 10, 20, 10);
    ctx.fillStyle = '#000';
    ctx.font = '12px Arial';
    ctx.fillText('High', width - 125, 20);
    
    ctx.fillStyle = '#4444ff';
    ctx.fillRect(width - 150, 25, 20, 10);
    ctx.fillStyle = '#000';
    ctx.fillText('Low', width - 125, 35);

  }, [data]);

  return (
    <canvas 
      ref={canvasRef} 
      width={800} 
      height={400} 
      style={{ border: '1px solid #ccc', margin: '20px' }}
    />
  );
};

export default ChartCanvas;
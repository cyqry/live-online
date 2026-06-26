import React, { useEffect, useRef, useState } from 'react';
import './index.css';

const MeteorBackground = () => {
    const canvasRef = useRef(null);
    const cursorRef = useRef(null);
    const [animationEnabled, setAnimationEnabled] = useState(true);
    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext('2d');
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        const particles = [];
        const colors = ['#f9c80e', '#f86624', '#ea3546', '#662e9b', '#43bccd'];

        class Particle {
            constructor(x, y, radius, color) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.color = color;
            }

            draw() {
                ctx.beginPath();
                ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2, false);
                ctx.fillStyle = this.color;
                ctx.fill();
                ctx.closePath();
            }
        }

        function createParticles(e) {
            const canvasBounds = canvas.getBoundingClientRect();
            const xPos = e.clientX - canvasBounds.left;
            const yPos = e.clientY - canvasBounds.top;

            for (let i = 0; i < 10; i++) {
                const offsetX = (Math.random() - 0.5) * 20;
                const offsetY = (Math.random() - 0.5) * 20;
                particles.push(new Particle(xPos + offsetX, yPos + offsetY, Math.random() * 2, colors[Math.floor(Math.random() * colors.length)]));

            }
        }



        function animateParticles() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            for (let i = 0; i < particles.length; i++) {
                particles[i].draw();
                particles[i].x += (Math.random() - 0.5) * 5;
                particles[i].y += (Math.random() - 0.5) * 5;
                particles[i].radius -= 0.1;

                if (particles[i].radius < 0) {
                    particles.splice(i, 1);
                    i--;
                }
            }

            requestAnimationFrame(animateParticles);
        }

        function handleMouseMove(e) {
            const xPos = e.clientX;
            const yPos = e.clientY;
            cursorRef.current.style.left = `${xPos}px`;
            cursorRef.current.style.top = `${yPos}px`;
        }

        function handleMouseEnter() {
            cursorRef.current.style.display = 'block';
        }

        function handleMouseLeave() {
            cursorRef.current.style.display = 'none';
        }
        function handleResize() {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        }

        animateParticles();
        if (animationEnabled) {
            window.addEventListener('resize', handleResize);
            window.addEventListener('mousemove', createParticles);
            window.addEventListener('mousemove', handleMouseMove);
            canvas.addEventListener('mouseenter', handleMouseEnter);
            canvas.addEventListener('mouseleave', handleMouseLeave);
        } else {
            window.removeEventListener('mousemove', createParticles);
            window.removeEventListener('mousemove', handleMouseMove);
            canvas.removeEventListener('mouseenter', handleMouseEnter);
            canvas.removeEventListener('mouseleave', handleMouseLeave);
            window.removeEventListener('resize', handleResize);
        }
        let tag = Math.random()
        log("effect" + tag)

        return () => {
            log("回调" + tag)
            window.removeEventListener('mousemove', createParticles);
            window.removeEventListener('mousemove', handleMouseMove);
            canvas.removeEventListener('mouseenter', handleMouseEnter);
            canvas.removeEventListener('mouseleave', handleMouseLeave);
            window.removeEventListener('resize', handleResize);
        };
    }, [animationEnabled]);

    return (
        <div className="canvas-container">
            <canvas id="meteor-canvas" ref={canvasRef}></canvas>
            <div className="custom-cursor" ref={cursorRef}></div>
            <button
                style={{
                    position: 'fixed',
                    top: '10px',
                    right: '10px',
                    zIndex: 1,
                    backgroundColor: "red"
                }}
                onClick={() => setAnimationEnabled(!animationEnabled)}
            >
                {animationEnabled ? '关闭动画' : '开启动画'}
            </button>
        </div>
    );
};

export default MeteorBackground;
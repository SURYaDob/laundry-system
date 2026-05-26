/* ============================================================
   AquaClean Luxe — Premium Dark Theme Dashboard JS
   Animations · Charts · Interactions · Toast System
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
    'use strict';

    // ===== 1. SIDEBAR TOGGLE (Mobile) =====
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const appSidebar = document.querySelector('.app-sidebar');

    if (sidebarToggle && appSidebar) {
        sidebarToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            appSidebar.classList.toggle('show');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (appSidebar.classList.contains('show') &&
                !appSidebar.contains(e.target) &&
                e.target !== sidebarToggle) {
                appSidebar.classList.remove('show');
            }
        });
    }

    // Mobile sidebar helper — show toggle only on small screens
    const toggleBtn = document.getElementById('sidebar-toggle');
    if (toggleBtn) {
        const updateToggleVisibility = () => {
            toggleBtn.style.display = window.innerWidth <= 768 ? 'block' : 'none';
        };
        updateToggleVisibility();
        window.addEventListener('resize', updateToggleVisibility);
    }

    // ===== 2. ACTIVE MENU ITEM HIGHLIGHTING =====
    const currentPath = window.location.pathname;
    document.querySelectorAll('.menu-link').forEach(link => {
        const linkPath = link.getAttribute('href');
        const parentItem = link.parentElement;
        if (currentPath === linkPath ||
            (linkPath && linkPath !== '/' && currentPath.startsWith(linkPath))) {
            parentItem.classList.add('active');
        } else {
            parentItem.classList.remove('active');
        }
    });

    // ===== 3. ALERT AUTO-DISMISS WITH FADE =====
    document.querySelectorAll('.alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // ===== 4. SCROLL-TRIGGERED FADE-IN ANIMATIONS =====
    const fadeElements = document.querySelectorAll('.fade-in-up');
    if (fadeElements.length > 0) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });

        fadeElements.forEach(el => observer.observe(el));
    }

    // ===== 5. STAT CARD COUNTER ANIMATION =====
    document.querySelectorAll('.stat-value').forEach(el => {
        const text = el.textContent;
        // Only animate numeric values (plain numbers)
        const num = parseFloat(text.replace(/[₹,]/g, ''));
        if (!isNaN(num) && text === String(Math.round(num))) {
            const duration = 800;
            const startTime = performance.now();
            const startVal = 0;

            function animateCounter(currentTime) {
                const elapsed = currentTime - startTime;
                const progress = Math.min(elapsed / duration, 1);
                const eased = 1 - Math.pow(1 - progress, 3); // easeOutCubic
                const current = Math.floor(eased * num);
                el.textContent = current.toLocaleString();
                if (progress < 1) {
                    requestAnimationFrame(animateCounter);
                } else {
                    el.textContent = num.toLocaleString();
                }
            }
            requestAnimationFrame(animateCounter);
        }
    });

    // ===== 6. TABLE ROW HOVER GLOW =====
    document.querySelectorAll('.custom-table tbody tr').forEach(row => {
        row.addEventListener('mouseenter', () => {
            row.style.transition = 'background 0.2s ease';
        });
    });

    // ===== 7. GLOWING INPUT FOCUS =====
    document.querySelectorAll('.form-input').forEach(input => {
        input.addEventListener('focus', () => {
            input.parentElement?.querySelector('.form-label')?.style.setProperty('color', 'var(--neon-cyan)');
        });
        input.addEventListener('blur', () => {
            input.parentElement?.querySelector('.form-label')?.style.removeProperty('color');
        });
    });
});

// ===== 8. GLOBAL TOAST NOTIFICATION SYSTEM =====
window.showToast = (message, type = 'info') => {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        Object.assign(container.style, {
            position: 'fixed',
            top: '20px',
            right: '20px',
            zIndex: '99999',
            display: 'flex',
            flexDirection: 'column',
            gap: '10px'
        });
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const icons = {
        success: 'fa-check-circle',
        danger: 'fa-exclamation-triangle',
        error: 'fa-exclamation-triangle',
        warning: 'fa-exclamation-circle',
        info: 'fa-info-circle'
    };
    const colors = {
        success: { bg: 'linear-gradient(135deg, #10b981, #059669)', glow: 'rgba(16,185,129,0.3)' },
        danger: { bg: 'linear-gradient(135deg, #ef4444, #dc2626)', glow: 'rgba(239,68,68,0.3)' },
        error: { bg: 'linear-gradient(135deg, #ef4444, #dc2626)', glow: 'rgba(239,68,68,0.3)' },
        warning: { bg: 'linear-gradient(135deg, #f59e0b, #d97706)', glow: 'rgba(245,158,11,0.3)' },
        info: { bg: 'linear-gradient(135deg, #00f0ff, #0ea5e9)', glow: 'rgba(0,240,255,0.3)' }
    };

    const c = colors[type] || colors.info;
    toast.className = `toast-alert toast-${type}`;
    Object.assign(toast.style, {
        padding: '14px 22px',
        borderRadius: '12px',
        color: '#ffffff',
        fontWeight: '600',
        fontSize: '14px',
        fontFamily: "'Outfit', sans-serif",
        background: c.bg,
        boxShadow: `0 8px 32px ${c.glow}, 0 4px 8px rgba(0,0,0,0.2)`,
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        transform: 'translateX(120%)',
        transition: 'transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
        border: '1px solid rgba(255,255,255,0.1)',
        backdropFilter: 'blur(10px)',
        minWidth: '280px'
    });

    toast.innerHTML = `<i class="fas ${icons[type] || icons.info}" style="font-size:16px;"></i> <span>${message}</span>`;
    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.transform = 'translateX(0)';
    });

    setTimeout(() => {
        toast.style.transform = 'translateX(120%)';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
};

// ===== 9. CHART.JS DARK THEME HELPER =====
window.initDashboardChart = (canvasId, type, labels, data, datasetLabel, colors = {}) => {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    const isDark = true;
    const gridColor = isDark ? 'rgba(148, 163, 184, 0.08)' : 'rgba(226, 232, 240, 0.5)';
    const textColor = isDark ? '#94a3b8' : '#64748b';

    const defaults = {
        bg: isDark ? 'rgba(0, 240, 255, 0.08)' : 'rgba(2, 132, 199, 0.1)',
        border: isDark ? '#00f0ff' : '#0284c7',
        pointHoverBg: isDark ? '#a855f7' : '#4f46e5'
    };

    const finalColors = { ...defaults, ...colors };

    return new Chart(ctx, {
        type: type,
        data: {
            labels: labels,
            datasets: [{
                label: datasetLabel,
                data: data,
                backgroundColor: type === 'doughnut' || type === 'pie'
                    ? (colors.bg || ['rgba(0,240,255,0.3)', 'rgba(168,85,247,0.3)', 'rgba(16,185,129,0.3)', 'rgba(245,158,11,0.3)', 'rgba(239,68,68,0.3)'])
                    : finalColors.bg,
                borderColor: type === 'doughnut' || type === 'pie'
                    ? (colors.border || ['#00f0ff', '#a855f7', '#10b981', '#f59e0b', '#ef4444'])
                    : finalColors.border,
                borderWidth: type === 'doughnut' || type === 'pie' ? 2 : 2,
                pointBackgroundColor: finalColors.border,
                pointBorderColor: isDark ? '#0a0f1e' : '#ffffff',
                pointHoverBackgroundColor: finalColors.pointHoverBg,
                pointHoverRadius: 6,
                tension: 0.35,
                fill: type === 'line'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: {
                    display: type === 'pie' || type === 'doughnut',
                    labels: {
                        color: textColor,
                        font: { family: "'Outfit', sans-serif", size: 12 },
                        padding: 16,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    backgroundColor: isDark ? 'rgba(17, 24, 39, 0.95)' : 'rgba(255, 255, 255, 0.95)',
                    titleFont: { family: "'Outfit', sans-serif", weight: '700' },
                    bodyFont: { family: "'Outfit', sans-serif" },
                    borderColor: isDark ? 'rgba(0, 240, 255, 0.2)' : 'rgba(2, 132, 199, 0.2)',
                    borderWidth: 1,
                    padding: 12,
                    cornerRadius: 8,
                    titleColor: isDark ? '#f1f5f9' : '#1e293b',
                    bodyColor: isDark ? '#94a3b8' : '#64748b'
                }
            },
            scales: type === 'pie' || type === 'doughnut' ? {} : {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: gridColor,
                        drawBorder: false
                    },
                    ticks: {
                        color: textColor,
                        font: { family: "'Outfit', sans-serif", size: 11 },
                        padding: 8
                    }
                },
                x: {
                    grid: { display: false },
                    ticks: {
                        color: textColor,
                        font: { family: "'Outfit', sans-serif", size: 11 },
                        padding: 8
                    }
                }
            }
        }
    });
};

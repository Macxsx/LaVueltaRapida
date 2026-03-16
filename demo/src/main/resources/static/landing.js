// ── Swiper Carousel ──────────────────────────────────────────
new Swiper('.menu-swiper', {
    slidesPerView: 1.2,
    spaceBetween: 20,
    loop: true,
    grabCursor: true,
    autoplay: {
        delay: 3500,
        disableOnInteraction: false,
        pauseOnMouseEnter: true,
    },
    navigation: {
        nextEl: '.carousel-btn--next',
        prevEl: '.carousel-btn--prev',
    },
    pagination: {
        el: '.carousel-pagination',
        clickable: true,
    },
    breakpoints: {
        640:  { slidesPerView: 2.2, spaceBetween: 20 },
        1024: { slidesPerView: 3.4, spaceBetween: 24 },
        1280: { slidesPerView: 4.4, spaceBetween: 24 },
    },
});

// ── Hero Stats Counter ───────────────────────────────────────
function animateCounter(el, target, duration) {
    const suffix  = el.dataset.suffix || '';
    const isFloat = target % 1 !== 0;
    const start   = performance.now();

    function update(now) {
        const elapsed  = now - start;
        const progress = Math.min(elapsed / duration, 1);
        const eased    = 1 - Math.pow(1 - progress, 3);
        const value    = isFloat
            ? (target * eased).toFixed(1)
            : Math.round(target * eased);
        el.textContent = value + suffix;
        if (progress < 1) requestAnimationFrame(update);
    }
    requestAnimationFrame(update);
}

const statsSection = document.getElementById('hero-stats');
if (statsSection) {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                statsSection.querySelectorAll('.stat-value[data-target]').forEach(el => {
                    animateCounter(el, parseFloat(el.dataset.target), 1800);
                });
                observer.disconnect();
            }
        });
    }, { threshold: 0.5 });
    observer.observe(statsSection);
}

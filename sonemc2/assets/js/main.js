function initFaqAccordions() {
    const faqItems = document.querySelectorAll('.faq-item');
    
    faqItems.forEach(item => {
        const question = item.querySelector('.faq-question');
        
        question.addEventListener('click', () => {
            faqItems.forEach(otherItem => {
                if (otherItem !== item && otherItem.classList.contains('active')) {
                    otherItem.classList.remove('active');
                }
            });
            
            item.classList.toggle('active');
        });
    });
}

function initProjectFilter() {
    const filterButtons = document.querySelectorAll('.filter-btn');
    const projectCards = document.querySelectorAll('.project-card');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            const filter = button.getAttribute('data-filter');
            
            projectCards.forEach(card => {
                const categories = card.getAttribute('data-categories');
                
                if (filter === 'all' || categories.includes(filter)) {
                    card.style.display = '';
                } else {
                    card.style.display = 'none';
                }                
            });
        });
    });
}

function animateOnScroll() {
    const elements = document.querySelectorAll('.about-content, .projects-grid, .faq-container, .contact-container');
    
    const observerCallback = (entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-fade-in');
                observer.unobserve(entry.target);
            }
        });
    };
    
    const observer = new IntersectionObserver(observerCallback, {
        root: null,
        threshold: 0.1,
        rootMargin: '-50px'
    });
    
    elements.forEach(element => {
        element.classList.add('animate-on-scroll');
        observer.observe(element);
    });
}

function animateStats() {
    const stats = document.querySelectorAll('.stat-number');
    
    const observerCallback = (entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const target = entry.target;
                const value = parseInt(target.textContent);
                let current = 0;
                const duration = 2000;
                const step = Math.max(1, Math.floor(value / (duration / 30)));
                
//                const counter = setInterval(() => {
//                    current += step;
//                    if (current >= value) {
//                        target.textContent = value + '+';
//                        clearInterval(counter);
//                    } else {
//                        target.textContent = current;
//                    }
//                }, 30);
                
                observer.unobserve(target);
            }
        });
    };
    
    const observer = new IntersectionObserver(observerCallback, {
        root: null,
        threshold: 0.1
    });
    
    stats.forEach(stat => {
        observer.observe(stat);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initFaqAccordions();
    initProjectFilter();
    animateOnScroll();
    animateStats();
    
    document.querySelectorAll('.project-card').forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('animate-fade-in');
    });
    
    document.querySelectorAll('.stat-item').forEach((item, index) => {
        item.style.animationDelay = `${index * 0.2}s`;
        item.classList.add('animate-fade-in');
    });
});

const animationStyles = document.createElement('style');
animationStyles.innerHTML = `
    .animate-on-scroll {
        opacity: 0;
        transform: translateY(30px);
        transition: opacity 0.8s ease, transform 0.8s ease;
    }
    
    .animate-fade-in {
        animation: fadeIn 0.8s ease forwards;
    }
    
    .animate-on-scroll.animate-fade-in {
        opacity: 1;
        transform: translateY(0);
    }
    
    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
`;
document.head.appendChild(animationStyles);
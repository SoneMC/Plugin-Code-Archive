function handleSidebarSubmenus() {
    const sidebarItems = document.querySelectorAll('.sidebar-nav > div > ul > li');
    
    sidebarItems.forEach(item => {
        const link = item.querySelector('a');
        const submenu = item.querySelector('.sidebar-submenu');
        
        if (submenu) {
            link.innerHTML += ' <i class="fas fa-chevron-down" style="font-size: 0.75rem; margin-left: 5px;"></i>';
            
            const submenuLinks = submenu.querySelectorAll('a');
            const currentPath = window.location.pathname;
            
            let isActive = false;
            submenuLinks.forEach(sublink => {
                if (currentPath.includes(sublink.getAttribute('href'))) {
                    isActive = true;
                    sublink.classList.add('active');
                }
            });
            
            if (isActive || currentPath.includes(link.getAttribute('href'))) {
                item.classList.add('active');
                link.classList.add('active');
            }
            
            link.addEventListener('click', (e) => {
                e.preventDefault();
                item.classList.toggle('active');
            });
        }
    });
}

function addCopyButtons() {
    const copyButton = document.querySelector('.plugin-file-header .copy-button');
    
    if (copyButton) {
        const codeBlock = document.querySelector('pre code'); 

        copyButton.addEventListener('click', () => {
            const code = codeBlock ? codeBlock.textContent : ''; 
            
            if (code) {
                navigator.clipboard.writeText(code).then(() => {
                    copyButton.textContent = 'Copied!';
                    copyButton.classList.add('copied');
                    
                    setTimeout(() => {
                        copyButton.textContent = 'Copy';
                        copyButton.classList.remove('copied');
                    }, 2000);
                }).catch(err => {
                    console.error('Failed to copy text: ', err);
                });
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    addCopyButtons();
});

function implementSearch() {
    const searchInput = document.getElementById('docsSearch');
    
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const sidebarLinks = document.querySelectorAll('.sidebar-nav a');
            
            if (searchTerm.length > 2) {
                sidebarLinks.forEach(link => {
                    const text = link.textContent.toLowerCase();
                    const parent = link.parentElement;
                    
                    if (text.includes(searchTerm)) {
                        parent.style.display = 'block';
                        link.innerHTML = link.textContent.replace(
                            new RegExp(searchTerm, 'gi'),
                            match => `<span class="search-highlight">${match}</span>`
                        );
                    } else {
                        parent.style.display = 'none';
                    }
                });
            } else {
                sidebarLinks.forEach(link => {
                    const parent = link.parentElement;
                    parent.style.display = 'block';
                    link.innerHTML = link.textContent;
                });
            }
        });
    }
}

function addAnchorLinks() {
    const headings = document.querySelectorAll('.docs-section h2, .docs-section h3');
    
    headings.forEach(heading => {
        if (heading.id) {
            const anchor = document.createElement('a');
            anchor.className = 'anchor-link';
            anchor.href = `#${heading.id}`;
            anchor.innerHTML = '<i class="fas fa-link"></i>';
            anchor.title = 'Direct link to this section';
            
            heading.appendChild(anchor);
        }
    });
}

function highlightCurrentSection() {
    const headings = document.querySelectorAll('.docs-section h2[id], .docs-section h3[id]');
    
    const observerCallback = (entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const id = entry.target.getAttribute('id');
                const sidebarLink = document.querySelector(`.sidebar-nav a[href="#${id}"]`);
                
                if (sidebarLink) {
                    document.querySelectorAll('.sidebar-nav .sidebar-submenu a').forEach(link => {
                        link.classList.remove('active');
                    });
                    
                    sidebarLink.classList.add('active');
                    
                    const parentLi = sidebarLink.closest('li');
                    if (parentLi) {
                        parentLi.classList.add('active');
                    }
                }
            }
        });
    };
    
    const observer = new IntersectionObserver(observerCallback, {
        rootMargin: '-100px 0px -50% 0px',
        threshold: 0
    });
    
    headings.forEach(heading => observer.observe(heading));
}

function enhanceCodeBlocks() {
    const codeLines = document.querySelectorAll('.hljs-line');
    
    codeLines.forEach(line => {
        line.addEventListener('mouseenter', () => {
            line.classList.add('line-highlight');
        });
        
        line.addEventListener('mouseleave', () => {
            line.classList.remove('line-highlight');
        });
    });
}

document.addEventListener('DOMContentLoaded', function() {
    handleSidebarSubmenus();
    addCopyButtons();
    implementSearch();
    addAnchorLinks();
    highlightCurrentSection();
    enhanceCodeBlocks();
    
    const anchorStyles = document.createElement('style');
    anchorStyles.innerHTML = `
        .anchor-link {
            opacity: 0;
            margin-left: 8px;
            font-size: 0.8em;
            color: var(--gray-light);
            transition: var(--transition-fast);
        }
        
        h2:hover .anchor-link,
        h3:hover .anchor-link {
            opacity: 1;
        }
        
        .search-highlight {
            background-color: rgba(201, 6, 234, 0.2);
            border-radius: 2px;
            padding: 0 2px;
        }
        
        .line-highlight {
            background-color: rgba(255, 255, 255, 0.1);
        }
    `;
    document.head.appendChild(anchorStyles);
});

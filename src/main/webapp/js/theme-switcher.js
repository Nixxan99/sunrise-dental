/**
 * Theme Switcher Script for Sunrise Dental Clinic
 * Handles light/dark mode persistence via localStorage and Bootstrap 5 data-bs-theme
 */

(function () {
    'use strict';

    const STORAGE_KEY = 'sunrise_dental_theme';

    function getStoredTheme() {
        return localStorage.getItem(STORAGE_KEY);
    }

    function setStoredTheme(theme) {
        localStorage.setItem(STORAGE_KEY, theme);
    }

    function getPreferredTheme() {
        const storedTheme = getStoredTheme();
        if (storedTheme) {
            return storedTheme;
        }
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function setTheme(theme) {
        document.documentElement.setAttribute('data-bs-theme', theme);
        updateToggleButton(theme);
    }

    function updateToggleButton(theme) {
        const toggleButtons = document.querySelectorAll('#theme-toggle-btn, .theme-toggle-btn');
        toggleButtons.forEach(btn => {
            const icon = btn.querySelector('i');
            if (icon) {
                if (theme === 'dark') {
                    icon.className = 'bi bi-sun-fill text-warning';
                    btn.setAttribute('title', 'Switch to Light Mode');
                } else {
                    icon.className = 'bi bi-moon-stars-fill text-white';
                    btn.setAttribute('title', 'Switch to Dark Mode');
                }
            }
        });
    }

    // Apply preferred theme immediately to avoid flashing
    const initialTheme = getPreferredTheme();
    setTheme(initialTheme);

    window.addEventListener('DOMContentLoaded', () => {
        setTheme(getPreferredTheme());

        document.querySelectorAll('#theme-toggle-btn, .theme-toggle-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const currentTheme = document.documentElement.getAttribute('data-bs-theme') || 'light';
                const nextTheme = currentTheme === 'dark' ? 'light' : 'dark';
                setStoredTheme(nextTheme);
                setTheme(nextTheme);
            });
        });
    });

    // Listen for OS color scheme changes if user hasn't explicitly chosen
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
        const storedTheme = getStoredTheme();
        if (!storedTheme) {
            setTheme(getPreferredTheme());
        }
    });
})();

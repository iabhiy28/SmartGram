import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { 
  Home, 
  Wrench, 
  Briefcase, 
  Tractor, 
  AlertCircle, 
  FileText, 
  PhoneCall, 
  ShieldCheck, 
  Globe, 
  Bell, 
  User as UserIcon, 
  LogOut,
  Menu,
  X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Navbar: React.FC = () => {
  const { t, i18n } = useTranslation();
  const location = useLocation();
  const { user, isAuthenticated, logout, openAuthModal } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [langMenuOpen, setLangMenuOpen] = useState(false);

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
    setLangMenuOpen(false);
  };

  const navLinks = [
    { name: t('nav.home'), path: '/', icon: Home },
    { name: t('nav.services'), path: '/services', icon: Wrench },
    { name: t('nav.jobs'), path: '/jobs', icon: Briefcase },
    { name: t('nav.equipment'), path: '/equipment', icon: Tractor },
    { name: t('nav.complaints'), path: '/complaints', icon: AlertCircle },
    { name: t('nav.schemes'), path: '/schemes', icon: FileText },
    { name: t('nav.emergency'), path: '/emergency', icon: PhoneCall },
  ];

  if (user?.role === 'ROLE_PANCHAYAT_ADMIN' || user?.role === 'ROLE_SUPER_ADMIN') {
    navLinks.push({ name: t('nav.dashboard'), path: '/admin', icon: ShieldCheck });
  }

  return (
    <header className="glass-nav">
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '0 16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: '70px' }}>
          
          {/* Brand Logo */}
          <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px', textDecoration: 'none' }}>
            <div style={{
              width: '42px',
              height: '42px',
              borderRadius: '12px',
              background: 'linear-gradient(135deg, var(--primary-600) 0%, #064e3b 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffffff',
              boxShadow: '0 4px 10px rgba(5, 150, 105, 0.3)'
            }}>
              <Tractor size={24} />
            </div>
            <div>
              <span style={{ fontSize: '20px', fontWeight: '800', color: 'var(--slate-900)', fontFamily: 'Outfit' }}>
                Gram<span style={{ color: 'var(--primary-600)' }}>Connect</span>
              </span>
              <span style={{ display: 'block', fontSize: '10px', color: 'var(--slate-500)', marginTop: '-4px', fontWeight: '600', letterSpacing: '0.05em' }}>
                ಗ್ರಾಮೀಣ ಡಿಜಿಟಲ್ ವೇದಿಕೆ
              </span>
            </div>
          </Link>

          {/* Desktop Nav Links */}
          <nav style={{ display: 'none', gap: '6px', alignItems: 'center' }} className="desktop-nav">
            {navLinks.map((link) => {
              const Icon = link.icon;
              const isActive = location.pathname === link.path;
              return (
                <Link
                  key={link.path}
                  to={link.path}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '8px 14px',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '13.5px',
                    fontWeight: isActive ? '700' : '500',
                    color: isActive ? 'var(--primary-700)' : 'var(--slate-600)',
                    backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                    textDecoration: 'none',
                    transition: 'all 0.2s ease',
                  }}
                >
                  <Icon size={16} />
                  <span>{link.name}</span>
                </Link>
              );
            })}
          </nav>

          {/* Right Actions: Language, Notification, Auth */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            
            {/* Language Selector */}
            <div style={{ position: 'relative' }}>
              <button
                onClick={() => setLangMenuOpen(!langMenuOpen)}
                className="btn btn-secondary"
                style={{ padding: '6px 12px', fontSize: '13px' }}
                title="Change Language"
              >
                <Globe size={16} />
                <span>{i18n.language === 'hi' ? 'हिंदी' : i18n.language === 'kn' ? 'ಕನ್ನಡ' : 'EN'}</span>
              </button>

              {langMenuOpen && (
                <div style={{
                  position: 'absolute',
                  right: 0,
                  top: '100%',
                  marginTop: '8px',
                  backgroundColor: '#ffffff',
                  borderRadius: 'var(--radius-md)',
                  boxShadow: 'var(--card-shadow-hover)',
                  border: '1px solid var(--slate-200)',
                  width: '120px',
                  zIndex: 60,
                  overflow: 'hidden'
                }}>
                  <button
                    onClick={() => changeLanguage('en')}
                    style={{ width: '100%', padding: '10px 14px', textAlign: 'left', border: 'none', background: i18n.language === 'en' ? 'var(--primary-50)' : 'none', cursor: 'pointer', fontSize: '13px' }}
                  >
                    English
                  </button>
                  <button
                    onClick={() => changeLanguage('hi')}
                    style={{ width: '100%', padding: '10px 14px', textAlign: 'left', border: 'none', background: i18n.language === 'hi' ? 'var(--primary-50)' : 'none', cursor: 'pointer', fontSize: '13px' }}
                  >
                    हिंदी (Hindi)
                  </button>
                  <button
                    onClick={() => changeLanguage('kn')}
                    style={{ width: '100%', padding: '10px 14px', textAlign: 'left', border: 'none', background: i18n.language === 'kn' ? 'var(--primary-50)' : 'none', cursor: 'pointer', fontSize: '13px' }}
                  >
                    ಕನ್ನಡ (Kannada)
                  </button>
                </div>
              )}
            </div>

            {/* Auth Buttons */}
            {isAuthenticated ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '6px 12px',
                  backgroundColor: 'var(--slate-100)',
                  borderRadius: 'var(--radius-md)'
                }}>
                  <UserIcon size={16} color="var(--primary-600)" />
                  <span style={{ fontSize: '13px', fontWeight: '600' }}>{user?.fullName.split(' ')[0]}</span>
                </div>
                <button
                  onClick={logout}
                  className="btn btn-secondary"
                  style={{ padding: '6px 10px' }}
                  title="Logout"
                >
                  <LogOut size={16} />
                </button>
              </div>
            ) : (
              <button
                onClick={openAuthModal}
                className="btn btn-primary"
                style={{ padding: '8px 16px', fontSize: '13px' }}
              >
                <UserIcon size={16} />
                <span>{t('nav.login')}</span>
              </button>
            )}

            {/* Mobile menu trigger */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="btn btn-secondary mobile-menu-btn"
              style={{ display: 'none', padding: '6px 10px' }}
            >
              {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>

          </div>
        </div>

        {/* Mobile Navigation Dropdown */}
        {mobileMenuOpen && (
          <div style={{ padding: '12px 0 16px 0', borderTop: '1px solid var(--slate-200)' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {navLinks.map((link) => {
                const Icon = link.icon;
                const isActive = location.pathname === link.path;
                return (
                  <Link
                    key={link.path}
                    to={link.path}
                    onClick={() => setMobileMenuOpen(false)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      padding: '10px 14px',
                      borderRadius: 'var(--radius-md)',
                      fontSize: '14px',
                      fontWeight: isActive ? '700' : '500',
                      color: isActive ? 'var(--primary-700)' : 'var(--slate-700)',
                      backgroundColor: isActive ? 'var(--primary-50)' : 'transparent',
                      textDecoration: 'none',
                    }}
                  >
                    <Icon size={18} />
                    <span>{link.name}</span>
                  </Link>
                );
              })}
            </div>
          </div>
        )}
      </div>

      <style>{`
        @media (min-width: 900px) {
          .desktop-nav { display: flex !important; }
          .mobile-menu-btn { display: none !important; }
        }
        @media (max-width: 899px) {
          .desktop-nav { display: none !important; }
          .mobile-menu-btn { display: inline-flex !important; }
        }
      `}</style>
    </header>
  );
};

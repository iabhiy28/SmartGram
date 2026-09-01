import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { 
  Wrench, 
  Briefcase, 
  Tractor, 
  AlertCircle, 
  FileText, 
  PhoneCall, 
  ArrowRight, 
  CheckCircle2, 
  ShieldAlert, 
  Users, 
  Clock,
  Sparkles
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const HomePage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated } = useAuth();

  const quickPillars = [
    {
      title: t('services.title'),
      desc: 'Connect with verified local electricians, carpenters, plumbers, and mechanics with transparent rate cards.',
      path: '/services',
      icon: Wrench,
      color: '#059669',
      bgColor: '#ecfdf5',
      badge: '18 Verified'
    },
    {
      title: t('jobs.title'),
      desc: 'Find daily farm work, harvesting assistance, and construction jobs with fixed wages.',
      path: '/jobs',
      icon: Briefcase,
      color: '#d97706',
      bgColor: '#fffbeb',
      badge: '7 Openings'
    },
    {
      title: t('equipment.title'),
      desc: 'Rent tractors, rotavators, and harvesters with double-booking prevention.',
      path: '/equipment',
      icon: Tractor,
      color: '#2563eb',
      bgColor: '#eff6ff',
      badge: '12 Machines'
    },
    {
      title: t('complaints.title'),
      desc: 'Report water supply, road, and power issues directly to Panchayat with guaranteed SLA timelines.',
      path: '/complaints',
      icon: AlertCircle,
      color: '#dc2626',
      bgColor: '#fef2f2',
      badge: '72h SLA'
    },
    {
      title: t('schemes.title'),
      desc: 'Check eligibility for PM-KISAN, PMAY-G, and Karnataka welfare schemes with 1-click evaluation.',
      path: '/schemes',
      icon: FileText,
      color: '#7c3aed',
      bgColor: '#f5f3ff',
      badge: '9 Schemes'
    },
    {
      title: 'Emergency Helpline',
      desc: 'Direct tap-to-call directory for village PHC, police, ambulance, electricity helpline, and vet care.',
      path: '/emergency',
      icon: PhoneCall,
      color: '#ea580c',
      bgColor: '#fff7ed',
      badge: '24x7 Active'
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '40px' }}>
      
      {/* Hero Section */}
      <div className="hero-gradient">
        <div style={{ maxWidth: '750px', position: 'relative', zIndex: 10 }}>
          
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '6px',
            backgroundColor: 'rgba(255, 255, 255, 0.15)',
            backdropFilter: 'blur(8px)',
            padding: '6px 14px',
            borderRadius: '9999px',
            fontSize: '12.5px',
            fontWeight: '600',
            marginBottom: '18px',
            border: '1px solid rgba(255, 255, 255, 0.2)'
          }}>
            <Sparkles size={15} color="#fef08a" />
            <span>Digital India Village Initiative — Ramanagara & Mandya</span>
          </div>

          <h1 style={{ fontSize: '38px', lineHeight: '1.2', color: '#ffffff', marginBottom: '16px' }}>
            {t('hero.title')}
          </h1>

          <p style={{ fontSize: '16px', lineHeight: '1.6', color: 'rgba(255, 255, 255, 0.9)', marginBottom: '28px' }}>
            {t('hero.subtitle')}
          </p>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px' }}>
            <Link to="/services" className="btn btn-accent" style={{ padding: '12px 22px', fontSize: '15px' }}>
              <Wrench size={18} />
              <span>{t('hero.exploreServices')}</span>
            </Link>

            <Link to="/jobs" className="btn btn-secondary" style={{ padding: '12px 22px', fontSize: '15px', backgroundColor: 'rgba(255,255,255,0.9)' }}>
              <Briefcase size={18} />
              <span>{t('hero.viewJobs')}</span>
            </Link>

            <Link to="/equipment" className="btn btn-secondary" style={{ padding: '12px 22px', fontSize: '15px', backgroundColor: 'rgba(255,255,255,0.9)' }}>
              <Tractor size={18} />
              <span>{t('hero.rentTractor')}</span>
            </Link>

            <Link to="/complaints" className="btn btn-secondary" style={{ padding: '12px 22px', fontSize: '15px', backgroundColor: 'rgba(255,255,255,0.9)' }}>
              <AlertCircle size={18} />
              <span>{t('hero.fileComplaint')}</span>
            </Link>
          </div>

        </div>
      </div>

      {/* Village Statistics Bar */}
      <div style={{
        backgroundColor: '#ffffff',
        border: '1px solid var(--slate-200)',
        borderRadius: 'var(--radius-lg)',
        padding: '24px',
        boxShadow: 'var(--card-shadow)'
      }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '20px', textAlign: 'center' }}>
          <div>
            <div style={{ fontSize: '28px', fontWeight: '800', color: 'var(--primary-600)', fontFamily: 'Outfit' }}>100%</div>
            <div style={{ fontSize: '13px', color: 'var(--slate-500)', fontWeight: '500' }}>Verified Phone Access</div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: '800', color: 'var(--accent-600)', fontFamily: 'Outfit' }}>₹500+</div>
            <div style={{ fontSize: '13px', color: 'var(--slate-500)', fontWeight: '500' }}>Daily Farm Wage</div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: '800', color: '#2563eb', fontFamily: 'Outfit' }}>45 HP+</div>
            <div style={{ fontSize: '13px', color: 'var(--slate-500)', fontWeight: '500' }}>Tractors for Rent</div>
          </div>
          <div>
            <div style={{ fontSize: '28px', fontWeight: '800', color: '#dc2626', fontFamily: 'Outfit' }}>72 Hours</div>
            <div style={{ fontSize: '13px', color: 'var(--slate-500)', fontWeight: '500' }}>Max Grievance SLA</div>
          </div>
        </div>
      </div>

      {/* 6 Core Feature Cards */}
      <div>
        <div style={{ marginBottom: '24px' }}>
          <h2 style={{ fontSize: '26px', color: 'var(--slate-900)' }}>Explore Village Portals</h2>
          <p style={{ fontSize: '14px', color: 'var(--slate-500)', marginTop: '4px' }}>
            Instant digital access to all essential services in your Gram Panchayat.
          </p>
        </div>

        <div className="grid-3">
          {quickPillars.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className="card"
                style={{ textDecoration: 'none', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
              >
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                    <div style={{
                      width: '48px',
                      height: '48px',
                      borderRadius: '12px',
                      backgroundColor: item.bgColor,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: item.color
                    }}>
                      <Icon size={24} />
                    </div>
                    <span className="badge badge-neutral">{item.badge}</span>
                  </div>

                  <h3 style={{ fontSize: '18px', color: 'var(--slate-900)', marginBottom: '8px' }}>
                    {item.title}
                  </h3>

                  <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', lineHeight: '1.5' }}>
                    {item.desc}
                  </p>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: item.color, fontWeight: '700', fontSize: '13.5px', marginTop: '20px' }}>
                  <span>Open Portal</span>
                  <ArrowRight size={15} />
                </div>
              </Link>
            );
          })}
        </div>
      </div>

      {/* Banner: Register as a Service Provider or Equipment Owner */}
      {!isAuthenticated && (
        <div style={{
          backgroundColor: '#ffffff',
          border: '1px solid var(--primary-200)',
          borderRadius: 'var(--radius-xl)',
          padding: '32px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '20px',
          boxShadow: '0 10px 25px -5px rgba(5, 150, 105, 0.1)'
        }}>
          <div>
            <h3 style={{ fontSize: '22px', color: 'var(--slate-900)', marginBottom: '6px' }}>
              Are you an Electrician, Plumber, or Tractor Owner?
            </h3>
            <p style={{ fontSize: '14px', color: 'var(--slate-600)', maxWidth: '600px' }}>
              Register on GramConnect to receive direct bookings from villagers without middlemen commission.
            </p>
          </div>
          <button onClick={openAuthModal} className="btn btn-primary" style={{ padding: '12px 24px', fontSize: '15px' }}>
            Register Your Services Free
          </button>
        </div>
      )}

    </div>
  );
};

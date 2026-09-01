import React from 'react';
import { Tractor, Heart, Shield, Phone, Mail } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer style={{ backgroundColor: 'var(--slate-900)', color: 'var(--slate-300)', marginTop: '64px', borderTop: '1px solid var(--slate-800)' }}>
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '48px 16px 24px 16px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '32px', marginBottom: '36px' }}>
          
          {/* Col 1: Brand Info */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
              <div style={{
                width: '36px',
                height: '36px',
                borderRadius: '8px',
                background: 'var(--primary-600)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#ffffff'
              }}>
                <Tractor size={20} />
              </div>
              <span style={{ fontSize: '18px', fontWeight: '800', color: '#ffffff', fontFamily: 'Outfit' }}>
                Gram<span style={{ color: 'var(--primary-500)' }}>Connect</span>
              </span>
            </div>
            <p style={{ fontSize: '13.5px', lineHeight: '1.6', color: 'var(--slate-400)' }}>
              Empowering Indian villages through one united digital ecosystem for work, local services, agricultural machinery, and transparent grievance resolution.
            </p>
          </div>

          {/* Col 2: Fast Portals */}
          <div>
            <h4 style={{ color: '#ffffff', fontSize: '15px', marginBottom: '16px' }}>Village Services</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '13.5px' }}>
              <li><a href="/services" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>Electricians & Plumbers</a></li>
              <li><a href="/jobs" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>Harvesting & Farm Labor</a></li>
              <li><a href="/equipment" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>Tractor & Machinery Rental</a></li>
              <li><a href="/complaints" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>Civic Grievances & SLA</a></li>
            </ul>
          </div>

          {/* Col 3: Government Portals */}
          <div>
            <h4 style={{ color: '#ffffff', fontSize: '15px', marginBottom: '16px' }}>Government Portals</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '13.5px' }}>
              <li><a href="https://pmkisan.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>PM-KISAN Portal</a></li>
              <li><a href="https://nrega.nic.in" target="_blank" rel="noreferrer" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>MGNREGA Portal</a></li>
              <li><a href="https://pmayg.nic.in" target="_blank" rel="noreferrer" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>PMAY-Gramin Portal</a></li>
              <li><a href="https://sevasindhu.karnataka.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--slate-400)', textDecoration: 'none' }}>Seva Sindhu (Karnataka)</a></li>
            </ul>
          </div>

          {/* Col 4: Emergency Helpline */}
          <div>
            <h4 style={{ color: '#ffffff', fontSize: '15px', marginBottom: '16px' }}>Emergency Helpline</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '13.5px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Phone size={15} color="var(--urgent-500)" />
                <span style={{ fontWeight: '600', color: '#ffffff' }}>National Emergency: 112</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Phone size={15} color="var(--primary-500)" />
                <span>Kisan Call Center: 1800-180-1551</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Phone size={15} color="var(--accent-500)" />
                <span>Bidadi PHC: 080-27282222</span>
              </div>
            </div>
          </div>

        </div>

        {/* Bottom Bar */}
        <div style={{
          borderTop: '1px solid var(--slate-800)',
          paddingTop: '20px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '12px',
          fontSize: '12.5px',
          color: 'var(--slate-500)'
        }}>
          <div>
            © {new Date().getFullYear()} GramConnect. Built with ❤️ for rural Indian communities.
          </div>
          <div style={{ display: 'flex', gap: '16px' }}>
            <span>Privacy Policy</span>
            <span>Terms of Service</span>
            <span>Panchayat Guidelines</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

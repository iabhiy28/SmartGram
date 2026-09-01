import React from 'react';
import { Phone, Shield, Ambulance, Flame, HeartPulse, Zap } from 'lucide-react';

export const EmergencyDirectoryPage: React.FC = () => {
  const contacts = [
    {
      name: 'National Emergency Response Support',
      designation: 'Police / Fire / Medical',
      phone: '112',
      serviceType: 'NATIONAL HELPLINE',
      icon: Shield,
      color: '#dc2626',
      bgColor: '#fef2f2'
    },
    {
      name: 'Bidadi Primary Health Center (PHC)',
      designation: 'Medical Officer on Duty',
      phone: '080-27282222',
      serviceType: 'HOSPITAL & AMBULANCE',
      icon: Ambulance,
      color: '#059669',
      bgColor: '#ecfdf5'
    },
    {
      name: 'BESCOM Electricity Emergency Desk',
      designation: 'Bidadi Sub-Division Wireman',
      phone: '1912',
      serviceType: 'POWER & TRANSFORMER REPAIR',
      icon: Zap,
      color: '#d97706',
      bgColor: '#fffbeb'
    },
    {
      name: 'Kisan Agricultural Helpline',
      designation: 'Govt Agronomist on Call',
      phone: '1800-180-1551',
      serviceType: 'FARM & CROP SUPPORT',
      icon: HeartPulse,
      color: '#2563eb',
      bgColor: '#eff6ff'
    },
    {
      name: 'Ramanagara District Veterinary Clinic',
      designation: 'Livestock Veterinary Doctor',
      phone: '080-27271144',
      serviceType: 'CATTLE & ANIMAL HEALTH',
      icon: Flame,
      color: '#7c3aed',
      bgColor: '#f5f3ff'
    }
  ];

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>Village Emergency Directory</h1>
        <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
          One-tap emergency dialing for medical, electrical, and veterinary help in Bidadi Gram Panchayat.
        </p>
      </div>

      <div className="grid-2">
        {contacts.map((c, idx) => {
          const Icon = c.icon;
          return (
            <div key={idx} className="card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                <div style={{
                  width: '52px',
                  height: '52px',
                  borderRadius: '14px',
                  backgroundColor: c.bgColor,
                  color: c.color,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}>
                  <Icon size={26} />
                </div>
                <div>
                  <span className="badge badge-neutral" style={{ fontSize: '10.5px', marginBottom: '4px' }}>{c.serviceType}</span>
                  <h3 style={{ fontSize: '16px', color: 'var(--slate-900)' }}>{c.name}</h3>
                  <div style={{ fontSize: '13px', color: 'var(--slate-500)' }}>{c.designation}</div>
                </div>
              </div>

              <a
                href={`tel:${c.phone}`}
                className="btn btn-primary"
                style={{ backgroundColor: c.color, padding: '12px 18px', whiteSpace: 'nowrap', borderRadius: 'var(--radius-lg)' }}
              >
                <Phone size={16} />
                <span>Call {c.phone}</span>
              </a>
            </div>
          );
        })}
      </div>
    </div>
  );
};

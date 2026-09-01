import React, { useState } from 'react';
import { 
  ShieldCheck, 
  AlertCircle, 
  Briefcase, 
  Tractor, 
  Users, 
  CheckCircle, 
  Clock, 
  TrendingUp,
  Megaphone
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [broadcastModalOpen, setBroadcastModalOpen] = useState(false);
  const [broadcastSuccess, setBroadcastSuccess] = useState(false);

  const kpis = [
    { title: 'Registered Citizens', value: '1,420', change: '+12% this month', icon: Users, color: '#2563eb' },
    { title: 'Grievance SLA Rate', value: '94.2%', change: 'Avg 31h resolution', icon: AlertCircle, color: '#dc2626' },
    { title: 'Farm Wages Disbursed', value: '₹1.25 Lakh', change: '24 farm jobs', icon: Briefcase, color: '#059669' },
    { title: 'Machinery Rentals', value: '₹45,000', change: '18 tractor days', icon: Tractor, color: '#d97706' },
  ];

  const pendingComplaints = [
    { id: '#GC-8901', title: 'Main Pipeline Leakage', village: 'Bidadi', priority: 'HIGH', sla: '28h remaining' },
    { id: '#GC-8898', title: 'Streetlight Cable Damage', village: 'Kenchanakoppa', priority: 'MEDIUM', sla: '42h remaining' },
  ];

  const pendingVerifications = [
    { name: 'Suresh Kumar', trade: 'Electrician (Wireman License)', phone: '9845112299', village: 'Bidadi' },
    { name: 'Naveen Gowda', trade: 'Tractor Owner (Mahindra 575)', phone: '9880223344', village: 'Byramangala' },
  ];

  const handleBroadcast = (e: React.FormEvent) => {
    e.preventDefault();
    setBroadcastSuccess(true);
    setTimeout(() => {
      setBroadcastModalOpen(false);
      setBroadcastSuccess(false);
    }, 2000);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span className="badge badge-success">PANCHAYAT ADMIN</span>
            <span style={{ fontSize: '13px', color: 'var(--slate-500)' }}>Bidadi Gram Panchayat (Ramanagara Dist)</span>
          </div>
          <h1 style={{ fontSize: '28px', color: 'var(--slate-900)', marginTop: '4px' }}>
            Administrative Command Center
          </h1>
        </div>

        <button
          onClick={() => setBroadcastModalOpen(true)}
          className="btn btn-danger"
          style={{ padding: '10px 18px' }}
        >
          <Megaphone size={16} />
          <span>Publish Village Broadcast</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid-4" style={{ marginBottom: '32px' }}>
        {kpis.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <div key={idx} className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <span style={{ fontSize: '13px', color: 'var(--slate-500)', fontWeight: '600' }}>{kpi.title}</span>
                <div style={{ width: '36px', height: '36px', borderRadius: '8px', backgroundColor: 'var(--slate-100)', color: kpi.color, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon size={18} />
                </div>
              </div>
              <div style={{ fontSize: '26px', fontWeight: '800', color: 'var(--slate-900)', fontFamily: 'Outfit' }}>
                {kpi.value}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--primary-700)', fontWeight: '600', marginTop: '4px' }}>
                {kpi.change}
              </div>
            </div>
          );
        })}
      </div>

      {/* 2-Column Administrative Review Grid */}
      <div className="grid-2">
        
        {/* Column 1: SLA Grievance Queue */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Priority Grievances to Resolve</h3>
            <span className="badge badge-danger">2 Active</span>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {pendingComplaints.map((item) => (
              <div key={item.id} style={{ border: '1px solid var(--slate-200)', borderRadius: 'var(--radius-md)', padding: '14px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '6px' }}>
                  <div>
                    <span style={{ fontSize: '11px', fontWeight: '700', color: 'var(--slate-400)' }}>{item.id}</span>
                    <h4 style={{ fontSize: '15px', color: 'var(--slate-900)' }}>{item.title}</h4>
                    <span style={{ fontSize: '12.5px', color: 'var(--slate-500)' }}>Village: {item.village}</span>
                  </div>
                  <span className="badge badge-danger">{item.priority}</span>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px', paddingTop: '10px', borderTop: '1px dashed var(--slate-200)' }}>
                  <span style={{ fontSize: '12px', color: 'var(--urgent-600)', fontWeight: '600' }}>{item.sla}</span>
                  <button className="btn btn-primary" style={{ padding: '6px 12px', fontSize: '12px' }}>
                    Mark Resolved
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Column 2: Service Provider Verifications */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Provider Verification Queue</h3>
            <span className="badge badge-warning">2 Pending</span>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {pendingVerifications.map((p, idx) => (
              <div key={idx} style={{ border: '1px solid var(--slate-200)', borderRadius: 'var(--radius-md)', padding: '14px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '6px' }}>
                  <div>
                    <h4 style={{ fontSize: '15px', color: 'var(--slate-900)' }}>{p.name}</h4>
                    <div style={{ fontSize: '13px', color: 'var(--primary-700)', fontWeight: '600' }}>{p.trade}</div>
                    <div style={{ fontSize: '12.5px', color: 'var(--slate-500)' }}>Phone: {p.phone} • {p.village}</div>
                  </div>
                </div>

                <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                  <button className="btn btn-secondary" style={{ flex: 1, padding: '6px 12px', fontSize: '12px' }}>
                    View Certificate
                  </button>
                  <button className="btn btn-primary" style={{ flex: 1, padding: '6px 12px', fontSize: '12px' }}>
                    Approve Badge
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>

      {/* Broadcast Modal */}
      {broadcastModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Publish Gram Panchayat Announcement</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Broadcast will be delivered in real-time to all registered villagers via SMS & Web push.
            </p>

            {broadcastSuccess ? (
              <div style={{ textAlign: 'center', padding: '24px 0' }}>
                <div style={{
                  width: '56px',
                  height: '56px',
                  borderRadius: '50%',
                  backgroundColor: 'var(--primary-100)',
                  color: 'var(--primary-600)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px auto'
                }}>
                  <CheckCircle size={32} />
                </div>
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Broadcast Dispatched!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  All 1,420 villagers in Bidadi Panchayat have received the alert.
                </p>
              </div>
            ) : (
              <form onSubmit={handleBroadcast}>
                <div className="form-group">
                  <label className="form-label">Broadcast Type</label>
                  <select className="form-select">
                    <option value="GENERAL">General Notice (Gram Sabha / Health Camp)</option>
                    <option value="EMERGENCY">Urgent Alert (Weather / Power Shutdown)</option>
                    <option value="SCHEME">Scheme Registration Drive</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Announcement Title</label>
                  <input type="text" placeholder="e.g. Free Cattle Vaccination Camp on Sep 08" className="form-input" required />
                </div>

                <div className="form-group">
                  <label className="form-label">Announcement Message</label>
                  <textarea className="form-textarea" rows={4} placeholder="Full details of date, venue, and instructions for villagers" required></textarea>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setBroadcastModalOpen(false)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-danger" style={{ flex: 1 }}>
                    Broadcast to Village
                  </button>
                </div>
              </form>
            )}

          </div>
        </div>
      )}

    </div>
  );
};

import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AlertCircle, ThumbsUp, Clock, PlusCircle, CheckCircle2, MapPin, ShieldAlert } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const ComplaintsPage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated } = useAuth();
  const [fileComplaintModalOpen, setFileComplaintModalOpen] = useState(false);
  const [fileSuccess, setFileSuccess] = useState(false);

  const [complaints, setComplaints] = useState([
    {
      id: 'c1',
      title: 'Main Pipeline Leakage near Bidadi Bus Stand',
      category: 'Drinking Water',
      description: 'Drinking water pipe cracked causing severe water wastage and muddy road for the past 2 days.',
      village: 'Bidadi Village',
      status: 'IN_PROGRESS',
      slaRemaining: '28h remaining',
      upvotes: 42,
      filedAt: 'Aug 30, 2026',
      priority: 'HIGH'
    },
    {
      id: 'c2',
      title: '5 Streetlights Non-Functional on Temple Road',
      category: 'Electricity & Lighting',
      description: 'Complete darkness on the pathway to the Gram Panchayat office at night. High risk for elders.',
      village: 'Kenchanakoppa',
      status: 'UNDER_REVIEW',
      slaRemaining: '48h remaining',
      upvotes: 18,
      filedAt: 'Aug 31, 2026',
      priority: 'MEDIUM'
    },
    {
      id: 'c3',
      title: 'Drainage Overflow near Government Primary School',
      category: 'Sanitation & Waste',
      description: 'Blocked drain causing stagnant wastewater pool outside school entrance. Mosquito breeding hazard.',
      village: 'Byramangala',
      status: 'RESOLVED',
      slaRemaining: 'Resolved in 24h',
      upvotes: 65,
      filedAt: 'Aug 28, 2026',
      priority: 'CRITICAL'
    }
  ]);

  const handleUpvote = (id: string) => {
    setComplaints((prev) =>
      prev.map((c) => (c.id === id ? { ...c, upvotes: c.upvotes + 1 } : c))
    );
  };

  const handleOpenFile = () => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    setFileComplaintModalOpen(true);
    setFileSuccess(false);
  };

  const submitComplaint = (e: React.FormEvent) => {
    e.preventDefault();
    setFileSuccess(true);
    setTimeout(() => {
      setFileComplaintModalOpen(false);
      setFileSuccess(false);
    }, 2000);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>{t('complaints.title')}</h1>
          <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
            {t('complaints.subtitle')}
          </p>
        </div>

        <button onClick={handleOpenFile} className="btn btn-danger" style={{ padding: '10px 20px' }}>
          <PlusCircle size={17} />
          <span>{t('complaints.fileNew')}</span>
        </button>
      </div>

      {/* Complaint Cards Grid */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {complaints.map((c) => {
          const isResolved = c.status === 'RESOLVED';
          return (
            <div key={c.id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
              
              {/* Left Column: Details */}
              <div style={{ flex: 1, minWidth: '280px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                  <span className={`badge ${
                    c.status === 'RESOLVED' ? 'badge-success' : c.status === 'IN_PROGRESS' ? 'badge-warning' : 'badge-danger'
                  }`}>
                    {c.status.replace('_', ' ')}
                  </span>
                  <span className="badge badge-neutral">{c.category}</span>
                  {c.priority === 'CRITICAL' && <span className="badge badge-danger">CRITICAL</span>}
                </div>

                <h3 style={{ fontSize: '18px', color: 'var(--slate-900)', marginBottom: '6px' }}>
                  {c.title}
                </h3>

                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', lineHeight: '1.5', marginBottom: '10px' }}>
                  {c.description}
                </p>

                <div style={{ display: 'flex', alignItems: 'center', gap: '16px', fontSize: '12.5px', color: 'var(--slate-500)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <MapPin size={14} />
                    <span>{c.village}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: isResolved ? 'var(--primary-600)' : 'var(--accent-700)', fontWeight: '600' }}>
                    <Clock size={14} />
                    <span>{c.slaRemaining}</span>
                  </div>
                </div>
              </div>

              {/* Right Column: Upvote Button */}
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
                <button
                  onClick={() => handleUpvote(c.id)}
                  className="btn btn-secondary"
                  style={{ display: 'flex', flexDirection: 'column', padding: '10px 16px', minWidth: '70px', borderRadius: 'var(--radius-lg)' }}
                >
                  <ThumbsUp size={18} color="var(--primary-600)" />
                  <span style={{ fontWeight: '800', fontSize: '15px', color: 'var(--slate-900)' }}>{c.upvotes}</span>
                </button>
                <span style={{ fontSize: '11px', color: 'var(--slate-400)' }}>Supporters</span>
              </div>

            </div>
          );
        })}
      </div>

      {/* File Grievance Modal */}
      {fileComplaintModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>File a Civic Grievance</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Gram Panchayat SLA tracking begins immediately upon submission.
            </p>

            {fileSuccess ? (
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
                  <CheckCircle2 size={32} />
                </div>
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Grievance Registered!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  Your complaint ID is #GC-8902. Panchayat officers have 72 hours to acknowledge and resolve.
                </p>
              </div>
            ) : (
              <form onSubmit={submitComplaint}>
                <div className="form-group">
                  <label className="form-label">Issue Category</label>
                  <select className="form-select" required>
                    <option value="WATER">Drinking Water & Borewell (48h SLA)</option>
                    <option value="ELECTRICITY">Street Light & Power Outage (24h SLA)</option>
                    <option value="ROADS">Potholes & Road Damage (72h SLA)</option>
                    <option value="SANITATION">Drainage & Garbage Waste (48h SLA)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Grievance Title</label>
                  <input type="text" placeholder="Short summary of the issue" className="form-input" required />
                </div>

                <div className="form-group">
                  <label className="form-label">Detailed Description & Landmarks</label>
                  <textarea className="form-textarea" rows={3} placeholder="Explain exactly where and what the problem is" required></textarea>
                </div>

                <div className="form-group">
                  <label className="form-label">Priority Level</label>
                  <select className="form-select">
                    <option value="MEDIUM">Medium (Standard)</option>
                    <option value="HIGH">High (Affecting multiple families)</option>
                    <option value="CRITICAL">Critical (Health/Safety hazard)</option>
                  </select>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setFileComplaintModalOpen(false)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-danger" style={{ flex: 1 }}>
                    Submit Grievance
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

import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Briefcase, Calendar, MapPin, Users, PlusCircle, CheckCircle2, DollarSign } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const JobsPage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated, user } = useAuth();
  const [applyModalJob, setApplyModalJob] = useState<any | null>(null);
  const [postJobModalOpen, setPostJobModalOpen] = useState(false);
  const [applySuccess, setApplySuccess] = useState(false);
  const [postSuccess, setPostSuccess] = useState(false);

  const jobs = [
    {
      id: 'j1',
      title: 'Paddy Harvesting Labor (Rabi Crop)',
      employer: 'Shankar Gowda (Bidadi Farm)',
      village: 'Bidadi Village',
      dailyWage: '₹550',
      workersNeeded: 6,
      workersAccepted: 4,
      startDate: 'Sep 05, 2026',
      endDate: 'Sep 09, 2026',
      skills: 'Sickle harvesting, paddy bundling',
      status: 'OPEN'
    },
    {
      id: 'j2',
      title: 'Mango Orchard Weeding & Pruning',
      employer: 'Venkatesh Rao (Ramanagara Orchards)',
      village: 'Kenchanakoppa',
      dailyWage: '₹600',
      workersNeeded: 4,
      workersAccepted: 4,
      startDate: 'Sep 03, 2026',
      endDate: 'Sep 06, 2026',
      skills: 'Tree pruning, grass weeding',
      status: 'FILLED'
    },
    {
      id: 'j3',
      title: 'Granary Compound Wall Mason Helpers',
      employer: 'Bidadi Farmers Cooperative',
      village: 'Byramangala',
      dailyWage: '₹500',
      workersNeeded: 3,
      workersAccepted: 1,
      startDate: 'Sep 04, 2026',
      endDate: 'Sep 10, 2026',
      skills: 'Cement mixing, brick hauling',
      status: 'OPEN'
    }
  ];

  const handleApply = (job: any) => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    setApplyModalJob(job);
    setApplySuccess(false);
  };

  const handlePostJob = () => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    setPostJobModalOpen(true);
    setPostSuccess(false);
  };

  const submitApplication = (e: React.FormEvent) => {
    e.preventDefault();
    setApplySuccess(true);
    setTimeout(() => {
      setApplyModalJob(null);
      setApplySuccess(false);
    }, 2000);
  };

  const submitPostJob = (e: React.FormEvent) => {
    e.preventDefault();
    setPostSuccess(true);
    setTimeout(() => {
      setPostJobModalOpen(false);
      setPostSuccess(false);
    }, 2000);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>{t('jobs.title')}</h1>
          <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
            {t('jobs.subtitle')}
          </p>
        </div>

        <button onClick={handlePostJob} className="btn btn-accent" style={{ padding: '10px 20px' }}>
          <PlusCircle size={17} />
          <span>{t('jobs.postJob')}</span>
        </button>
      </div>

      {/* Job Cards Grid */}
      <div className="grid-3">
        {jobs.map((job) => {
          const spotsLeft = job.workersNeeded - job.workersAccepted;
          const isFilled = job.status === 'FILLED' || spotsLeft <= 0;

          return (
            <div key={job.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                {/* Header: Title, Wage */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                  <h3 style={{ fontSize: '18px', color: 'var(--slate-900)', flex: 1, paddingRight: '8px' }}>
                    {job.title}
                  </h3>
                  <div style={{
                    backgroundColor: 'var(--accent-100)',
                    color: 'var(--accent-700)',
                    padding: '4px 10px',
                    borderRadius: 'var(--radius-sm)',
                    fontWeight: '800',
                    fontSize: '14px',
                    whiteSpace: 'nowrap'
                  }}>
                    {job.dailyWage} <span style={{ fontSize: '11px', fontWeight: '500' }}>/day</span>
                  </div>
                </div>

                {/* Employer & Village */}
                <div style={{ fontSize: '13px', color: 'var(--slate-600)', marginBottom: '14px' }}>
                  <strong>{job.employer}</strong> • {job.village}
                </div>

                {/* Duration & Skills */}
                <div style={{ fontSize: '13px', color: 'var(--slate-600)', display: 'flex', flexDirection: 'column', gap: '6px', marginBottom: '16px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Calendar size={14} color="var(--slate-400)" />
                    <span>{job.startDate} — {job.endDate}</span>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Briefcase size={14} color="var(--slate-400)" />
                    <span>{job.skills}</span>
                  </div>
                </div>

                {/* Capacity Progress Bar */}
                <div style={{ marginBottom: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12.5px', marginBottom: '4px', fontWeight: '600' }}>
                    <span style={{ color: 'var(--slate-600)' }}>Worker Capacity:</span>
                    <span style={{ color: isFilled ? 'var(--urgent-600)' : 'var(--primary-700)' }}>
                      {job.workersAccepted}/{job.workersNeeded} accepted ({isFilled ? t('jobs.filled') : `${spotsLeft} ${t('jobs.spotsLeft')}`})
                    </span>
                  </div>
                  <div style={{ width: '100%', height: '8px', backgroundColor: 'var(--slate-200)', borderRadius: '4px', overflow: 'hidden' }}>
                    <div style={{
                      width: `${(job.workersAccepted / job.workersNeeded) * 100}%`,
                      height: '100%',
                      backgroundColor: isFilled ? 'var(--urgent-500)' : 'var(--primary-600)',
                      transition: 'width 0.3s ease'
                    }} />
                  </div>
                </div>
              </div>

              {/* Action Button */}
              <button
                onClick={() => handleApply(job)}
                disabled={isFilled}
                className={`btn ${isFilled ? 'btn-secondary' : 'btn-primary'}`}
                style={{ width: '100%', padding: '10px', fontSize: '13.5px', opacity: isFilled ? 0.6 : 1 }}
              >
                {isFilled ? t('jobs.filled') : t('jobs.applyNow')}
              </button>

            </div>
          );
        })}
      </div>

      {/* Apply Modal */}
      {applyModalJob && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Apply for Rural Work</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Position: <strong>{applyModalJob.title}</strong> (Wage: {applyModalJob.dailyWage}/day)
            </p>

            {applySuccess ? (
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
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Application Submitted!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  The employer has received your application and will notify you when accepted.
                </p>
              </div>
            ) : (
              <form onSubmit={submitApplication}>
                <div className="form-group">
                  <label className="form-label">Applicant Name</label>
                  <input type="text" className="form-input" defaultValue={user?.fullName || 'Ramesh Kumar'} readOnly />
                </div>

                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input type="text" className="form-input" defaultValue={user?.phoneNumber || '9876543210'} readOnly />
                </div>

                <div className="form-group">
                  <label className="form-label">Experience & Notes for Farmer</label>
                  <textarea className="form-textarea" rows={3} placeholder="Mention any past agricultural experience (e.g. 5 years in paddy harvesting)" required></textarea>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setApplyModalJob(null)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                    Submit Application
                  </button>
                </div>
              </form>
            )}

          </div>
        </div>
      )}

      {/* Post Job Modal (Farmer / Employer) */}
      {postJobModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Post a Village Job Opportunity</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Broadcast daily wage work to local villagers in your Panchayat.
            </p>

            {postSuccess ? (
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
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Job Posted Successfully!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  Villagers can now view and apply for your opening.
                </p>
              </div>
            ) : (
              <form onSubmit={submitPostJob}>
                <div className="form-group">
                  <label className="form-label">Job Title</label>
                  <input type="text" placeholder="e.g. Sugarcane Cutting & Loading" className="form-input" required />
                </div>

                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Workers Needed</label>
                    <input type="number" min="1" max="50" defaultValue="5" className="form-input" required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Daily Wage (₹)</label>
                    <input type="number" min="100" defaultValue="550" className="form-input" required />
                  </div>
                </div>

                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Start Date</label>
                    <input type="date" className="form-input" required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">End Date</label>
                    <input type="date" className="form-input" required />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Work Description & Location</label>
                  <textarea className="form-textarea" rows={3} placeholder="Provide farm location and requirements" required></textarea>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setPostJobModalOpen(false)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-accent" style={{ flex: 1 }}>
                    Publish Job
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

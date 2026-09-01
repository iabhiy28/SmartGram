import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { FileText, CheckCircle2, Sparkles, ExternalLink, Bookmark, Check, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const SchemesPage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated } = useAuth();
  const [eligibilityModalOpen, setEligibilityModalOpen] = useState(false);
  const [screeningResult, setScreeningResult] = useState<any | null>(null);

  const [schemes] = useState([
    {
      id: 's1',
      title: 'PM Kisan Samman Nidhi (PM-KISAN)',
      type: 'CENTRAL SCHEME',
      department: 'Ministry of Agriculture & Farmers Welfare',
      benefit: '₹6,000 per year directly transferred to bank accounts in 3 equal installments of ₹2,000.',
      eligibility: ['Small and marginal landholder farmer families', 'Cultivable landholding in owner name', 'Aadhaar linked bank account'],
      link: 'https://pmkisan.gov.in'
    },
    {
      id: 's2',
      title: 'Pradhan Mantri Awas Yojana - Gramin (PMAY-G)',
      type: 'CENTRAL SCHEME',
      department: 'Ministry of Rural Development',
      benefit: 'Financial assistance of ₹1.20 Lakh in plains / ₹1.30 Lakh in hilly areas for pucca house construction.',
      eligibility: ['Houseless families and those living in kutcha/dilapidated houses', 'SECC 2011 deprivation criteria', 'No pucca house anywhere in India'],
      link: 'https://pmayg.nic.in'
    },
    {
      id: 's3',
      title: 'Gruha Lakshmi Scheme (Karnataka)',
      type: 'STATE SCHEME (KARNATAKA)',
      department: 'Dept of Women & Child Development',
      benefit: '₹2,000 monthly financial aid to the woman head of every household in Karnataka.',
      eligibility: ['Woman head named in BPL/APL/Antyodaya ration cards', 'Neither woman nor husband pays income tax or GST', 'Karnataka resident'],
      link: 'https://sevasindhu.karnataka.gov.in'
    },
    {
      id: 's4',
      title: 'Kisan Credit Card (KCC) Scheme',
      type: 'CENTRAL SCHEME',
      department: 'Reserve Bank of India & NABARD',
      benefit: 'Subsidized institutional farm credit up to ₹3 Lakh at an effective interest rate of 4% p.a.',
      eligibility: ['Owner cultivators, tenant farmers, and sharecroppers', 'Age 18 to 75 years', 'Valid agricultural land records'],
      link: 'https://www.nabard.org'
    }
  ]);

  const handleScreening = (e: React.FormEvent) => {
    e.preventDefault();
    // Automated eligibility evaluation simulation
    setScreeningResult({
      eligibleCount: 3,
      totalCount: 4,
      matchedSchemes: ['PM-KISAN', 'Gruha Lakshmi', 'Kisan Credit Card'],
    });
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>{t('schemes.title')}</h1>
          <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
            {t('schemes.subtitle')}
          </p>
        </div>

        <button
          onClick={() => {
            setEligibilityModalOpen(true);
            setScreeningResult(null);
          }}
          className="btn btn-primary"
          style={{ padding: '10px 20px', backgroundColor: '#7c3aed' }}
        >
          <Sparkles size={17} />
          <span>{t('schemes.checkEligibility')}</span>
        </button>
      </div>

      {/* Scheme Cards Grid */}
      <div className="grid-2">
        {schemes.map((s) => (
          <div key={s.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span className="badge badge-neutral" style={{ fontSize: '11px', fontWeight: '700' }}>
                  {s.type}
                </span>
                <span style={{ fontSize: '12px', color: 'var(--slate-500)' }}>{s.department}</span>
              </div>

              <h3 style={{ fontSize: '18px', color: 'var(--slate-900)', marginBottom: '10px' }}>
                {s.title}
              </h3>

              <div style={{ backgroundColor: 'var(--primary-50)', padding: '12px', borderRadius: 'var(--radius-md)', marginBottom: '14px' }}>
                <div style={{ fontSize: '12px', fontWeight: '700', color: 'var(--primary-800)', marginBottom: '4px' }}>
                  Direct Benefit:
                </div>
                <div style={{ fontSize: '13.5px', color: 'var(--primary-950)', lineHeight: '1.4' }}>
                  {s.benefit}
                </div>
              </div>

              <div style={{ marginBottom: '16px' }}>
                <div style={{ fontSize: '12.5px', fontWeight: '700', color: 'var(--slate-700)', marginBottom: '6px' }}>
                  Key Eligibility Rules:
                </div>
                <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '4px', fontSize: '13px', color: 'var(--slate-600)' }}>
                  {s.eligibility.map((rule, idx) => (
                    <li key={idx} style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <Check size={14} color="var(--primary-600)" />
                      <span>{rule}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '10px', marginTop: '12px' }}>
              <a
                href={s.link}
                target="_blank"
                rel="noreferrer"
                className="btn btn-secondary"
                style={{ flex: 1, padding: '10px', fontSize: '13px' }}
              >
                <ExternalLink size={15} />
                <span>{t('schemes.applyOfficial')}</span>
              </a>
              <button className="btn btn-primary" style={{ padding: '10px 16px', fontSize: '13px' }}>
                <Bookmark size={15} />
                <span>Save</span>
              </button>
            </div>

          </div>
        ))}
      </div>

      {/* Eligibility Screening Modal */}
      {eligibilityModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Instant Welfare Eligibility Check</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Answer 4 questions to evaluate your eligibility across all Central and State schemes.
            </p>

            {screeningResult ? (
              <div style={{ textAlign: 'center', padding: '20px 0' }}>
                <div style={{
                  width: '56px',
                  height: '56px',
                  borderRadius: '50%',
                  backgroundColor: '#f5f3ff',
                  color: '#7c3aed',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px auto'
                }}>
                  <Sparkles size={32} />
                </div>
                <h4 style={{ fontSize: '20px', color: 'var(--slate-900)', fontFamily: 'Outfit' }}>
                  You Qualify for {screeningResult.eligibleCount} Government Schemes!
                </h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', margin: '20px 0', textAlign: 'left' }}>
                  {screeningResult.matchedSchemes.map((name: string, i: number) => (
                    <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 14px', backgroundColor: 'var(--primary-50)', borderRadius: 'var(--radius-md)' }}>
                      <CheckCircle2 size={18} color="var(--primary-600)" />
                      <span style={{ fontSize: '14px', fontWeight: '600', color: 'var(--slate-900)' }}>{name}</span>
                    </div>
                  ))}
                </div>
                <button
                  onClick={() => setEligibilityModalOpen(false)}
                  className="btn btn-primary"
                  style={{ width: '100%', padding: '12px' }}
                >
                  View My Recommended Schemes
                </button>
              </div>
            ) : (
              <form onSubmit={handleScreening}>
                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Applicant Age</label>
                    <input type="number" defaultValue="38" className="form-input" required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Gender</label>
                    <select className="form-select">
                      <option value="MALE">Male</option>
                      <option value="FEMALE">Female</option>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>
                </div>

                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Ration Card Category</label>
                    <select className="form-select">
                      <option value="BPL">BPL / Antyodaya</option>
                      <option value="APL">APL</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Annual Family Income</label>
                    <input type="number" defaultValue="95000" className="form-input" required />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Do you own agricultural land?</label>
                  <select className="form-select">
                    <option value="YES">Yes (Small/Marginal Holder &lt; 5 acres)</option>
                    <option value="NO">No (Landless Agricultural Laborer)</option>
                  </select>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setEligibilityModalOpen(false)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" style={{ flex: 1, backgroundColor: '#7c3aed' }}>
                    Run Eligibility Evaluation
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

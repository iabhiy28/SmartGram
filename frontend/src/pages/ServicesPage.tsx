import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Search, Star, ShieldCheck, Phone, MapPin, Calendar, Clock, Wrench } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const ServicesPage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [bookingModalProvider, setBookingModalProvider] = useState<any | null>(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);

  const categories = [
    { id: '1', name: 'Electrician', count: 6 },
    { id: '2', name: 'Plumber', count: 4 },
    { id: '3', name: 'Mason / Mistri', count: 5 },
    { id: '4', name: 'Carpenter', count: 3 },
    { id: '5', name: 'Appliance Repair', count: 2 },
  ];

  const providers = [
    {
      id: 'p1',
      name: 'Ramesh Patel',
      skill: 'Licensed Electrician',
      rating: 4.8,
      reviewsCount: 24,
      experience: '7 years exp',
      village: 'Bidadi Village',
      phone: '9876543210',
      isVerified: true,
      offerings: [
        { name: 'House Wiring & Repair', price: '₹350 / visit' },
        { name: 'Motor / Pump Starter Repair', price: '₹450 / job' }
      ]
    },
    {
      id: 'p2',
      name: 'Gopal Krishna',
      skill: 'Master Plumber',
      rating: 4.9,
      reviewsCount: 38,
      experience: '12 years exp',
      village: 'Kenchanakoppa',
      phone: '9845112233',
      isVerified: true,
      offerings: [
        { name: 'Borewell Pipe Fitting', price: '₹500 / job' },
        { name: 'Tap & Leakage Repair', price: '₹200 / visit' }
      ]
    },
    {
      id: 'p3',
      name: 'Basavaraj Gowda',
      skill: 'Mason & Construction Mistri',
      rating: 4.7,
      reviewsCount: 19,
      experience: '15 years exp',
      village: 'Byramangala',
      phone: '9880334455',
      isVerified: true,
      offerings: [
        { name: 'Wall Plastering & Brickwork', price: '₹800 / day' },
        { name: 'Compound Wall Construction', price: '₹750 / day' }
      ]
    }
  ];

  const handleBook = (provider: any) => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    setBookingModalProvider(provider);
    setBookingSuccess(false);
  };

  const submitBooking = (e: React.FormEvent) => {
    e.preventDefault();
    setBookingSuccess(true);
    setTimeout(() => {
      setBookingModalProvider(null);
      setBookingSuccess(false);
    }, 2000);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>{t('services.title')}</h1>
        <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
          {t('services.subtitle')}
        </p>
      </div>

      {/* Search & Category Filter Bar */}
      <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: '28px' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: '280px' }}>
          <input
            type="text"
            placeholder={t('services.searchPlaceholder')}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="form-input"
            style={{ paddingLeft: '40px', height: '46px' }}
          />
          <Search size={18} color="var(--slate-400)" style={{ position: 'absolute', left: '14px', top: '14px' }} />
        </div>

        <div style={{ display: 'flex', gap: '8px', overflowX: 'auto', paddingBottom: '4px' }}>
          <button
            onClick={() => setSelectedCategory(null)}
            className={`btn ${selectedCategory === null ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '8px 16px', fontSize: '13px', whiteSpace: 'nowrap' }}
          >
            All Categories
          </button>
          {categories.map((cat) => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(cat.name)}
              className={`btn ${selectedCategory === cat.name ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '8px 16px', fontSize: '13px', whiteSpace: 'nowrap' }}
            >
              {cat.name} ({cat.count})
            </button>
          ))}
        </div>
      </div>

      {/* Provider List Grid */}
      <div className="grid-3">
        {providers.map((p) => (
          <div key={p.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              {/* Header: Name, Skill, Verified Badge */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <div>
                  <h3 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>{p.name}</h3>
                  <div style={{ fontSize: '13.5px', color: 'var(--primary-700)', fontWeight: '600', marginTop: '2px' }}>
                    {p.skill}
                  </div>
                </div>
                {p.isVerified && (
                  <span className="badge badge-success">
                    <ShieldCheck size={13} />
                    <span>{t('services.verified')}</span>
                  </span>
                )}
              </div>

              {/* Rating & Location */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px', fontSize: '13px', color: 'var(--slate-600)', marginBottom: '16px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--accent-600)', fontWeight: '700' }}>
                  <Star size={15} fill="var(--accent-500)" color="var(--accent-500)" />
                  <span>{p.rating}</span>
                  <span style={{ color: 'var(--slate-400)', fontWeight: '400' }}>({p.reviewsCount})</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <MapPin size={14} />
                  <span>{p.village}</span>
                </div>
              </div>

              {/* Offerings Rate Card */}
              <div style={{ backgroundColor: 'var(--slate-50)', padding: '12px', borderRadius: 'var(--radius-md)', marginBottom: '16px' }}>
                <div style={{ fontSize: '12px', fontWeight: '700', color: 'var(--slate-500)', textTransform: 'uppercase', marginBottom: '6px' }}>
                  {t('services.rateCard')}
                </div>
                {p.offerings.map((off, idx) => (
                  <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', padding: '4px 0', borderBottom: idx === 0 ? '1px dashed var(--slate-200)' : 'none' }}>
                    <span>{off.name}</span>
                    <span style={{ fontWeight: '700', color: 'var(--slate-900)' }}>{off.price}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Action Buttons */}
            <div style={{ display: 'flex', gap: '10px' }}>
              <a
                href={`tel:${p.phone}`}
                className="btn btn-secondary"
                style={{ flex: 1, padding: '10px', fontSize: '13px' }}
              >
                <Phone size={15} />
                <span>Call</span>
              </a>
              <button
                onClick={() => handleBook(p)}
                className="btn btn-primary"
                style={{ flex: 1.5, padding: '10px', fontSize: '13px' }}
              >
                <Calendar size={15} />
                <span>{t('services.bookNow')}</span>
              </button>
            </div>

          </div>
        ))}
      </div>

      {/* Booking Modal */}
      {bookingModalProvider && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Book Service Appointment</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Booking <strong>{bookingModalProvider.name}</strong> ({bookingModalProvider.skill})
            </p>

            {bookingSuccess ? (
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
                  <ShieldCheck size={32} />
                </div>
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Booking Request Confirmed!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  The provider has received your phone number and will contact you shortly.
                </p>
              </div>
            ) : (
              <form onSubmit={submitBooking}>
                <div className="form-group">
                  <label className="form-label">Service Required</label>
                  <select className="form-select" required>
                    {bookingModalProvider.offerings.map((o: any, idx: number) => (
                      <option key={idx} value={o.name}>{o.name} — {o.price}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Preferred Date & Time</label>
                  <input type="datetime-local" className="form-input" required />
                </div>

                <div className="form-group">
                  <label className="form-label">Problem Details / House Address</label>
                  <textarea className="form-textarea" rows={3} placeholder="Describe the issue or provide landmark near your home" required></textarea>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setBookingModalProvider(null)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                    Confirm Booking
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

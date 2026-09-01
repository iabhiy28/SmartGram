import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Tractor, Calendar, MapPin, CheckCircle2, ShieldCheck, Gauge, PlusCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const EquipmentPage: React.FC = () => {
  const { t } = useTranslation();
  const { openAuthModal, isAuthenticated } = useAuth();
  const [bookingEquipment, setBookingEquipment] = useState<any | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [rateType, setRateType] = useState('DAILY');
  const [bookingSuccess, setBookingSuccess] = useState(false);

  const machinery = [
    {
      id: 'eq1',
      title: 'Mahindra 575 DI (45 HP) Tractor',
      owner: 'Suresh Patel',
      village: 'Bidadi Village',
      phone: '9876543210',
      dailyRate: 1500,
      hourlyRate: 250,
      make: 'Mahindra',
      horsePower: '45 HP',
      year: 2022,
      isOperational: true,
      serviceRadius: '15 km radius',
      attachments: 'Plow, Rotavator & Trolley'
    },
    {
      id: 'eq2',
      title: 'John Deere 5050D 4WD Heavy Harvester',
      owner: 'Kiran Gowda',
      village: 'Kenchanakoppa',
      phone: '9845223344',
      dailyRate: 3200,
      hourlyRate: 500,
      make: 'John Deere',
      horsePower: '50 HP',
      year: 2023,
      isOperational: true,
      serviceRadius: '25 km radius',
      attachments: 'Multi-crop Paddy & Wheat Harvester'
    },
    {
      id: 'eq3',
      title: 'Kirloskar 10 HP Diesel Irrigation Pump',
      owner: 'Manjunath Swamy',
      village: 'Byramangala',
      phone: '9880112299',
      dailyRate: 600,
      hourlyRate: 100,
      make: 'Kirloskar',
      horsePower: '10 HP',
      year: 2021,
      isOperational: true,
      serviceRadius: '10 km radius',
      attachments: '100m Delivery Pipe Included'
    }
  ];

  const handleBook = (eq: any) => {
    if (!isAuthenticated) {
      openAuthModal();
      return;
    }
    setBookingEquipment(eq);
    setBookingSuccess(false);
  };

  const calculateTotal = () => {
    if (!startDate || !endDate || !bookingEquipment) return 0;
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = end.getTime() - start.getTime();
    const diffDays = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1);
    
    if (rateType === 'DAILY') {
      return diffDays * bookingEquipment.dailyRate;
    } else {
      return diffDays * 8 * bookingEquipment.hourlyRate; // 8 hrs/day
    }
  };

  const submitBooking = (e: React.FormEvent) => {
    e.preventDefault();
    setBookingSuccess(true);
    setTimeout(() => {
      setBookingEquipment(null);
      setBookingSuccess(false);
    }, 2000);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '30px', color: 'var(--slate-900)' }}>{t('equipment.title')}</h1>
          <p style={{ fontSize: '15px', color: 'var(--slate-600)', marginTop: '4px' }}>
            {t('equipment.subtitle')}
          </p>
        </div>
      </div>

      {/* Equipment Listings Grid */}
      <div className="grid-3">
        {machinery.map((eq) => (
          <div key={eq.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <div>
              {/* Header: Title, Operational Badge */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <h3 style={{ fontSize: '18px', color: 'var(--slate-900)', flex: 1, paddingRight: '8px' }}>
                  {eq.title}
                </h3>
                <span className="badge badge-success">
                  <ShieldCheck size={13} />
                  <span>{t('equipment.operational')}</span>
                </span>
              </div>

              {/* Owner & Village */}
              <div style={{ fontSize: '13px', color: 'var(--slate-600)', marginBottom: '14px' }}>
                Owner: <strong>{eq.owner}</strong> • {eq.village}
              </div>

              {/* Specs */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', backgroundColor: 'var(--slate-50)', padding: '12px', borderRadius: 'var(--radius-md)', marginBottom: '16px', fontSize: '12.5px' }}>
                <div><strong>Power:</strong> {eq.horsePower}</div>
                <div><strong>Make:</strong> {eq.make} ({eq.year})</div>
                <div style={{ gridColumn: 'span 2' }}><strong>Included:</strong> {eq.attachments}</div>
              </div>

              {/* Rates */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', padding: '0 4px' }}>
                <div>
                  <span style={{ fontSize: '20px', fontWeight: '800', color: 'var(--primary-700)', fontFamily: 'Outfit' }}>
                    ₹{eq.dailyRate}
                  </span>
                  <span style={{ fontSize: '12px', color: 'var(--slate-500)' }}> /day</span>
                </div>
                <div>
                  <span style={{ fontSize: '16px', fontWeight: '700', color: 'var(--slate-700)' }}>
                    ₹{eq.hourlyRate}
                  </span>
                  <span style={{ fontSize: '12px', color: 'var(--slate-500)' }}> /hr</span>
                </div>
              </div>
            </div>

            {/* Action Button */}
            <button
              onClick={() => handleBook(eq)}
              className="btn btn-primary"
              style={{ width: '100%', padding: '10px', fontSize: '13.5px' }}
            >
              <Calendar size={15} />
              <span>{t('equipment.bookEquipment')}</span>
            </button>

          </div>
        ))}
      </div>

      {/* Date-Range Booking Modal */}
      {bookingEquipment && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '20px', marginBottom: '4px' }}>Rent Agricultural Machinery</h3>
            <p style={{ fontSize: '13.5px', color: 'var(--slate-500)', marginBottom: '20px' }}>
              Machine: <strong>{bookingEquipment.title}</strong> (Owner: {bookingEquipment.owner})
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
                  <CheckCircle2 size={32} />
                </div>
                <h4 style={{ fontSize: '18px', color: 'var(--slate-900)' }}>Booking Reserved!</h4>
                <p style={{ fontSize: '13.5px', color: 'var(--slate-600)', marginTop: '6px' }}>
                  Dates locked! The owner ({bookingEquipment.phone}) has received your rental request.
                </p>
              </div>
            ) : (
              <form onSubmit={submitBooking}>
                <div className="form-group">
                  <label className="form-label">Rental Type</label>
                  <select
                    value={rateType}
                    onChange={(e) => setRateType(e.target.value)}
                    className="form-select"
                  >
                    <option value="DAILY">Daily Rental (₹{bookingEquipment.dailyRate}/day)</option>
                    <option value="HOURLY">Hourly Rate (₹{bookingEquipment.hourlyRate}/hour)</option>
                  </select>
                </div>

                <div className="grid-2">
                  <div className="form-group">
                    <label className="form-label">Start Date</label>
                    <input
                      type="date"
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                      className="form-input"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">End Date</label>
                    <input
                      type="date"
                      value={endDate}
                      onChange={(e) => setEndDate(e.target.value)}
                      className="form-input"
                      required
                    />
                  </div>
                </div>

                {startDate && endDate && (
                  <div style={{
                    backgroundColor: 'var(--primary-50)',
                    border: '1px solid var(--primary-200)',
                    padding: '14px',
                    borderRadius: 'var(--radius-md)',
                    marginBottom: '16px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}>
                    <span style={{ fontSize: '13.5px', color: 'var(--primary-900)', fontWeight: '600' }}>Estimated Total:</span>
                    <span style={{ fontSize: '20px', fontWeight: '800', color: 'var(--primary-700)', fontFamily: 'Outfit' }}>
                      ₹{calculateTotal()}
                    </span>
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Land Location & Field Requirements</label>
                  <textarea className="form-textarea" rows={2} placeholder="e.g. 3-acre field near Bidadi lake" required></textarea>
                </div>

                <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
                  <button type="button" onClick={() => setBookingEquipment(null)} className="btn btn-secondary" style={{ flex: 1 }}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                    Confirm Reservation
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

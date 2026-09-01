import React, { useState } from 'react';
import { X, Phone, Lock, User, MapPin, Briefcase } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const AuthModal: React.FC = () => {
  const { isAuthModalOpen, closeAuthModal, login, register } = useAuth();
  const [isRegister, setIsRegister] = useState(false);
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [role, setRole] = useState('ROLE_VILLAGER');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (!isAuthModalOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (isRegister) {
        await register({
          phoneNumber,
          password,
          fullName,
          role,
          villageId: '43b5d863-5333-4fc2-9386-642d7be3819e', // Default Bidadi Village
        });
      } else {
        await login(phoneNumber, password);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: '440px' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div>
            <h3 style={{ fontSize: '20px', color: 'var(--slate-900)' }}>
              {isRegister ? 'Create GramConnect Account' : 'Welcome to GramConnect'}
            </h3>
            <p style={{ fontSize: '13px', color: 'var(--slate-500)', marginTop: '2px' }}>
              {isRegister ? 'Join your village digital community' : 'Enter your mobile number to sign in'}
            </p>
          </div>
          <button
            onClick={closeAuthModal}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--slate-400)' }}
          >
            <X size={20} />
          </button>
        </div>

        {error && (
          <div style={{
            padding: '10px 14px',
            backgroundColor: 'var(--urgent-50)',
            border: '1px solid #fca5a5',
            borderRadius: 'var(--radius-md)',
            color: 'var(--urgent-600)',
            fontSize: '13px',
            marginBottom: '16px'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <div style={{ position: 'relative' }}>
                <input
                  type="text"
                  required
                  placeholder="e.g. Ramesh Kumar"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="form-input"
                  style={{ paddingLeft: '38px' }}
                />
                <User size={16} color="var(--slate-400)" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              </div>
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Mobile Number</label>
            <div style={{ position: 'relative' }}>
              <input
                type="tel"
                required
                maxLength={10}
                placeholder="10-digit phone (e.g. 9876543210)"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value.replace(/\D/g, ''))}
                className="form-input"
                style={{ paddingLeft: '38px' }}
              />
              <Phone size={16} color="var(--slate-400)" style={{ position: 'absolute', left: '12px', top: '12px' }} />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type="password"
                required
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="form-input"
                style={{ paddingLeft: '38px' }}
              />
              <Lock size={16} color="var(--slate-400)" style={{ position: 'absolute', left: '12px', top: '12px' }} />
            </div>
          </div>

          {isRegister && (
            <div className="form-group">
              <label className="form-label">I am joining as:</label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="form-select"
              >
                <option value="ROLE_VILLAGER">Villager / Citizen</option>
                <option value="ROLE_SERVICE_PROVIDER">Service Provider (Electrician, Plumber, Mason)</option>
                <option value="ROLE_EMPLOYER">Farmer / Employer (Hiring labor or equipment owner)</option>
                <option value="ROLE_PANCHAYAT_ADMIN">Panchayat Administrator</option>
              </select>
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ width: '100%', padding: '12px', marginTop: '8px' }}
          >
            {loading ? 'Processing...' : isRegister ? 'Register Account' : 'Sign In'}
          </button>
        </form>

        {/* Toggle Switch */}
        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: 'var(--slate-600)' }}>
          {isRegister ? (
            <span>
              Already have an account?{' '}
              <button
                type="button"
                onClick={() => setIsRegister(false)}
                style={{ background: 'none', border: 'none', color: 'var(--primary-600)', fontWeight: '700', cursor: 'pointer' }}
              >
                Sign In
              </button>
            </span>
          ) : (
            <span>
              Don't have an account yet?{' '}
              <button
                type="button"
                onClick={() => setIsRegister(true)}
                style={{ background: 'none', border: 'none', color: 'var(--primary-600)', fontWeight: '700', cursor: 'pointer' }}
              >
                Register Here
              </button>
            </span>
          )}
        </div>

      </div>
    </div>
  );
};

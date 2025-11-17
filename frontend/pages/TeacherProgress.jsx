// src/pages/TeacherProgress.jsx
import React, { useEffect, useMemo, useRef, useState } from 'react';
import teacherProgressService from '../services/teacherProgressService';
import { Toaster, toast } from 'react-hot-toast';

// Small, inline progress bar component (works in dark and light)
const Bar = ({ value = 0 }) => (
  <div style={{
    width: '100%', height: 10, borderRadius: 999,
    background: 'var(--bar-bg, rgba(255,255,255,0.08))'
  }}>
    <div style={{
      width: `${Math.min(100, Math.max(0, value))}%`,
      height: '100%',
      borderRadius: 999,
      background: 'var(--bar-fill, #3b82f6)',
      transition: 'width .3s ease'
    }}/>
  </div>
);

// Simple modal for per-student details
const Modal = ({ open, onClose, title, children }) => {
  if (!open) return null;
  return (
    <div style={styles.modalBackdrop} onClick={onClose}>
      <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div style={styles.modalHeader}>
          <h3 style={{ margin: 0 }}>{title}</h3>
          <button onClick={onClose} style={styles.closeBtn}>×</button>
        </div>
        <div style={styles.modalBody}>{children}</div>
      </div>
    </div>
  );
};

const TeacherProgress = () => {
  const [rows, setRows] = useState([]);              // table rows
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState(null);    // selected StudentProgressSummaryDTO
  const [detailOpen, setDetailOpen] = useState(false);

  // Debounce search
  const t = useRef(null);
  useEffect(() => {
    setLoading(true);
    if (t.current) clearTimeout(t.current);

    t.current = setTimeout(async () => {
      try {
        const fn = query.trim() ? teacherProgressService.searchProgress
                                : teacherProgressService.getAllProgress;
        const data = await fn(query.trim());
        setRows(data || []);
      } catch (e) {
        toast.error('Failed to load progress');
      } finally {
        setLoading(false);
      }
    }, 350);

    return () => clearTimeout(t.current);
  }, [query]);

  useEffect(() => {
    // initial load
    (async () => {
      try {
        const data = await teacherProgressService.getAllProgress();
        setRows(data || []);
      } catch (e) {
        toast.error('Failed to load progress');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const subjectsCompleted = useMemo(() => (s) => {
    const list = s?.progressList || [];
    const completed = list.filter(i => i.completed);
    return `${completed.length}/${list.length}`;
  }, []);

  const openDetails = async (studentId) => {
    try {
      toast.loading('Loading details…', { id: 'detail' });
      const data = await teacherProgressService.getStudentProgress(studentId);
      setSelected(data);
      setDetailOpen(true);
      toast.success('Ready', { id: 'detail' });
    } catch (e) {
      toast.error('Could not load student details', { id: 'detail' });
    }
  };

  return (
    <div style={styles.page}>
      <Toaster position="top-right" />
      <div style={styles.header}>
        <h1 style={styles.title}>Students’ Progress</h1>

        {/* search + filters row */}
        <div style={styles.toolsRow}>
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by student name…"
            aria-label="Search students"
            style={styles.search}
          />
        </div>
      </div>

      {/* table / cards */}
      <div style={styles.card}>
        {loading ? (
          <div style={styles.loading}>Loading…</div>
        ) : rows.length === 0 ? (
          <div style={styles.empty}>No students found.</div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Student</th>
                  <th style={styles.th}>Std</th>
                  <th style={styles.th}>Lectures</th>
                  <th style={styles.th}>Materials</th>
                  <th style={styles.th}>Overall</th>
                  <th style={styles.th}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.studentId} style={styles.tr}>
                    <td style={styles.tdName}>
                      <div style={{ fontWeight: 600 }}>{r.studentName}</div>
                      <div style={styles.subtle}>
                        {subjectsCompleted(r)} completed
                      </div>
                    </td>
                    <td style={styles.td}>{r.studentStandard}</td>
                    <td style={styles.td}>
                      <div style={styles.smallRow}>
                        <Bar value={(r.completedLectures / Math.max(1, r.totalLectures)) * 100}/>
                        <span style={styles.mono}>
                          {r.completedLectures}/{r.totalLectures}
                        </span>
                      </div>
                    </td>
                    <td style={styles.td}>
                      <div style={styles.smallRow}>
                        <Bar value={(r.completedMaterials / Math.max(1, r.totalMaterials)) * 100}/>
                        <span style={styles.mono}>
                          {r.completedMaterials}/{r.totalMaterials}
                        </span>
                      </div>
                    </td>
                    <td style={styles.td}>
                      <div style={styles.smallRow}>
                        <Bar value={r.overallPercentage}/>
                        <span style={styles.mono}>{Math.round(r.overallPercentage)}%</span>
                      </div>
                    </td>
                    <td style={styles.td}>
                      <button
                        onClick={() => openDetails(r.studentId)}
                        style={styles.viewBtn}
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Mobile cards (shown by CSS at narrow widths) */}
            <div style={styles.mobileList}>
              {rows.map((r) => (
                <div key={r.studentId} style={styles.mobileCard}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                    <div>
                      <div style={{ fontWeight: 700 }}>{r.studentName}</div>
                      <div style={styles.subtle}>Std {r.studentStandard}</div>
                    </div>
                    <button onClick={() => openDetails(r.studentId)} style={styles.viewBtn}>View</button>
                  </div>

                  <div style={styles.mobileRow}>
                    <span style={styles.badge}>Lectures</span>
                    <Bar value={(r.completedLectures / Math.max(1, r.totalLectures)) * 100}/>
                    <span style={styles.mono}>{r.completedLectures}/{r.totalLectures}</span>
                  </div>

                  <div style={styles.mobileRow}>
                    <span style={styles.badge}>Materials</span>
                    <Bar value={(r.completedMaterials / Math.max(1, r.totalMaterials)) * 100}/>
                    <span style={styles.mono}>{r.completedMaterials}/{r.totalMaterials}</span>
                  </div>

                  <div style={styles.mobileRow}>
                    <span style={styles.badge}>Overall</span>
                    <Bar value={r.overallPercentage}/>
                    <span style={styles.mono}>{Math.round(r.overallPercentage)}%</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* detail modal */}
      <Modal
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        title={selected ? `Progress — ${selected.studentName} (Std ${selected.studentStandard})` : 'Progress'}
      >
        {!selected ? (
          <div>Loading…</div>
        ) : selected.progressList?.length === 0 ? (
          <div style={styles.empty}>No activity yet.</div>
        ) : (
          <div style={{ display: 'grid', gap: 12 }}>
            {selected.progressList.map((p) => (
              <div key={`${p.contentType}_${p.contentId}`} style={styles.detailRow}>
                <div style={{ fontWeight: 600 }}>
                  {p.title} <span style={styles.pill}>{p.contentType}</span>
                </div>
                <div style={styles.subtle}>{p.subject || '—'}</div>
                <div style={styles.detailMeta}>
                  <span style={p.completed ? styles.completed : styles.pending}>
                    {p.completed ? 'Completed' : 'Pending'}
                  </span>
                  {p.completionDate && (
                    <span style={styles.subtle}>
                      • {new Date(p.completionDate).toLocaleString()}
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </Modal>
    </div>
  );
};

/* ----------------- styles ----------------- */
const styles = {
  page: {
    padding: '20px',
    color: 'var(--fg, #e5e7eb)',
    background: 'transparent',
  },
  header: { display: 'grid', gap: 12, marginBottom: 16 },
  title: { margin: 0, fontSize: 28, fontWeight: 800 },
  toolsRow: { display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' },
  search: {
    flex: 1,
    minWidth: 220,
    padding: '10px 12px',
    borderRadius: 10,
    border: '1px solid rgba(255,255,255,0.12)',
    background: 'rgba(255,255,255,0.04)',
    color: 'inherit',
    outline: 'none',
  },
  card: {
    borderRadius: 14,
    border: '1px solid rgba(255,255,255,0.08)',
    background: 'rgba(0,0,0,0.25)',
    padding: 16
  },
  loading: { padding: 24, textAlign: 'center' },
  empty: { padding: 16, opacity: 0.7 },
  table: {
    width: '100%',
    borderCollapse: 'separate',
    borderSpacing: 0,
    display: 'table',
  },
  th: {
    textAlign: 'left',
    fontWeight: 700,
    fontSize: 13,
    padding: '12px 10px',
    borderBottom: '1px solid rgba(255,255,255,0.08)',
    whiteSpace: 'nowrap'
  },
  tr: { borderBottom: '1px solid rgba(255,255,255,0.06)' },
  td: { padding: '12px 10px', verticalAlign: 'middle' },
  tdName: { padding: '12px 10px', minWidth: 220 },
  subtle: { opacity: 0.7, fontSize: 12 },
  smallRow: { display: 'grid', gridTemplateColumns: '1fr auto', gap: 10, alignItems: 'center' },
  mono: { fontVariantNumeric: 'tabular-nums', opacity: 0.85, fontSize: 12 },
  viewBtn: {
    padding: '8px 12px',
    borderRadius: 8,
    border: '1px solid rgba(255,255,255,0.15)',
    background: 'rgba(59,130,246,0.15)',
    color: 'inherit',
    cursor: 'pointer'
  },
  /* Mobile cards (hidden on wide screens) */
  mobileList: { display: 'none' },
  mobileCard: {
    border: '1px solid rgba(255,255,255,0.08)',
    borderRadius: 12,
    padding: 12,
    marginTop: 12,
    background: 'rgba(0,0,0,0.25)',
  },
  mobileRow: { display: 'grid', gridTemplateColumns: 'auto 1fr auto', gap: 8, alignItems: 'center', marginTop: 8 },
  badge: {
    fontSize: 12, padding: '3px 8px', borderRadius: 999,
    background: 'rgba(255,255,255,0.08)'
  },
  pill: {
    marginLeft: 8,
    fontSize: 12,
    padding: '2px 8px',
    borderRadius: 999,
    background: 'rgba(59,130,246,0.2)'
  },
  /* Modal */
  modalBackdrop: {
    position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.45)',
    display: 'grid', placeItems: 'center', zIndex: 50
  },
  modal: {
    width: 'min(960px, 92vw)', maxHeight: '85vh', overflow: 'auto',
    background: 'var(--modal-bg, #0b1220)', color: 'inherit',
    border: '1px solid rgba(255,255,255,0.08)', borderRadius: 14, padding: 16
  },
  modalHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 },
  modalBody: { paddingTop: 8, display: 'grid', gap: 10 },
  closeBtn: {
    background: 'transparent', color: 'inherit', border: '1px solid rgba(255,255,255,0.2)',
    borderRadius: 8, cursor: 'pointer', width: 32, height: 32, lineHeight: '30px'
  },
  detailRow: {
    border: '1px solid rgba(255,255,255,0.08)',
    borderRadius: 10, padding: 10, background: 'rgba(255,255,255,0.03)'
  },
  detailMeta: { marginTop: 4, fontSize: 12, display: 'flex', gap: 8, alignItems: 'center' },
  completed: {
    color: '#22c55e',
    background: 'rgba(34,197,94,0.15)',
    padding: '2px 8px', borderRadius: 999, fontWeight: 600, fontSize: 12
  },
  pending: {
    color: '#f59e0b',
    background: 'rgba(245,158,11,0.15)',
    padding: '2px 8px', borderRadius: 999, fontWeight: 600, fontSize: 12
  }
};

/* Mobile breakpoint */
const styleEl = document.createElement('style');
styleEl.innerHTML = `
  @media (max-width: 820px) {
    table { display: none !important; }
    .mobile-only { display: block !important; }
  }
`;
document.head.appendChild(styleEl);

export default TeacherProgress;

// src/pages/StudentDashboard.jsx

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useAuth } from "../context/AuthContext";
import contentService from "../services/contentService";
import { toast } from "react-hot-toast";
import {
  FaDownload,
  FaCheckCircle,
  FaBookOpen,
  FaVideo,
  FaSpinner,
  FaPowerOff,
  FaSlidersH,
  FaTimes,
  FaChevronLeft,
} from "react-icons/fa";

/* ===========================================
   Small CSS injection for responsiveness & UX
   =========================================== */
const injectCSS = () => {
  if (document.getElementById("student-dashboard-css")) return;
  const css = `
  :root {
    --bg: #0d1117;
    --card: #161b22;
    --border: #30363d;
    --fg: #c9d1d9;
    --muted: #1f2430;
    --accent: #2563eb;
    --accentSoft: rgba(37,99,235,0.15);
    --radius: 12px;
    --pad: 12px;
    --gap: 12px;
    --font: 14px;
  }
  .sd-compact {
    --pad: 10px;
    --gap: 8px;
    --font: 13px;
  }
  body, html, #root {
    background: var(--bg) !important;
    color: var(--fg) !important;
    min-height: 100%;
  }
  .sd-page {
    min-height: 100vh;
    background: var(--bg);
    color: var(--fg);
    font-size: var(--font);
  }
  .sd-container {
    max-width: 1100px;
    margin: 0 auto;
    padding: 16px;
  }
  .sd-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--gap);
    padding: 8px 0;
  }
  .sd-title { margin: 0; }
  .sd-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border-radius: 8px;
    padding: 8px 12px;
    cursor: pointer;
    border: 1px solid var(--border);
    background: var(--card);
    color: var(--fg);
  }
  .sd-btn--danger { background: #ef4444; color: #fff; border-color: #ef4444; }
  .sd-btn--primary { background: var(--accent); color: #fff; border-color: var(--accent); }
  .sd-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--gap);
  }
  .sd-card {
    background: var(--card);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    padding: var(--pad);
  }
  .sd-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--pad);
  }
  .sd-filterbar {
    display: flex;
    gap: var(--gap);
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
  }
  .sd-chips {
    display: flex;
    gap: 8px;
    overflow-x: auto;
    scrollbar-width: thin;
    padding-bottom: 4px;
  }
  .sd-chip {
    padding: 6px 12px;
    border-radius: 999px;
    border: 1px solid var(--border);
    background: transparent;
    color: var(--fg);
    cursor: pointer;
    white-space: nowrap;
  }
  .sd-chip--active {
    background: var(--accentSoft);
    border-color: var(--accent);
  }
  .sd-input {
    min-width: 260px;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid var(--border);
    background: var(--card);
    color: var(--fg);
    outline: none;
  }
  .sd-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: var(--gap);
  }
  .sd-card__header {
    display: flex;
    align-items: center;
    border-bottom: 1px solid var(--border);
    padding-bottom: 8px;
    margin-bottom: 10px;
    gap: 10px;
  }
  .sd-card__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: var(--gap);
    margin-top: 12px;
  }
  .sd-btn--success {
    background: #22c55e;
    color: #fff;
    border: 0;
  }
  .sd-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    background: #064e3b;
    color: #a7f3d0;
    border: 1px solid #10b981;
    padding: 6px 10px;
    border-radius: 999px;
    font-weight: 600;
  }
  .sd-progress {
    width: 100%;
    height: 12px;
    background: #1f2430;
    border-radius: 8px;
    overflow: hidden;
  }
  .sd-progress__fill {
    height: 100%;
    background: linear-gradient(90deg, #2563eb, #1d4ed8);
    border-radius: 8px;
    transition: width 0.35s ease;
  }

  /* Settings full-screen */
  .sd-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,.6);
    z-index: 60;
  }
  .sd-settings {
    position: fixed;
    inset: 0;
    background: var(--bg);
    color: var(--fg);
    z-index: 70;
    display: grid;
    grid-template-rows: auto 1fr auto;
  }
  .sd-settings__header, .sd-settings__footer {
    padding: 12px 16px;
    border-bottom: 1px solid var(--border);
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .sd-settings__footer { border-top: 1px solid var(--border); border-bottom: none; justify-content: flex-end; }
  .sd-settings__content {
    padding: 16px;
    overflow: auto;
  }
  .sd-radio-row {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  .sd-select, .sd-radio, .sd-settings__input {
    accent-color: var(--accent);
  }

  /* Mobile tweaks */
  @media (max-width: 768px) {
    .sd-row { grid-template-columns: 1fr; }
    .sd-container { padding: 12px; }
    .sd-input { min-width: 100%; }
  }
  `;
  const el = document.createElement("style");
  el.id = "student-dashboard-css";
  el.textContent = css;
  document.head.appendChild(el);
};

// spinner CSS
const injectSpinner = () => {
  if (document.getElementById("student-dashboard-spin")) return;
  const el = document.createElement("style");
  el.id = "student-dashboard-spin";
  el.textContent = `.spin { animation: spin 1s linear infinite; } @keyframes spin { from {transform: rotate(0deg);} to {transform: rotate(360deg);} }`;
  document.head.appendChild(el);
};

/* ===========================================
   Theme & Density
   =========================================== */
const applyTheme = (mode) => {
  // light variables (only if needed)
  const setLight = () => {
    document.documentElement.style.setProperty("--bg", "#f5f7fb");
    document.documentElement.style.setProperty("--card", "#ffffff");
    document.documentElement.style.setProperty("--border", "#d7dce3");
    document.documentElement.style.setProperty("--fg", "#0f172a");
    document.documentElement.style.setProperty("--muted", "#e9eef6");
    document.body.style.backgroundColor = "#f5f7fb";
    document.body.style.color = "#0f172a";
  };
  const setDark = () => {
    document.documentElement.style.setProperty("--bg", "#0d1117");
    document.documentElement.style.setProperty("--card", "#161b22");
    document.documentElement.style.setProperty("--border", "#30363d");
    document.documentElement.style.setProperty("--fg", "#c9d1d9");
    document.documentElement.style.setProperty("--muted", "#1f2430");
    document.body.style.backgroundColor = "#0d1117";
    document.body.style.color = "#c9d1d9";
  };
  if (mode === "auto") {
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    prefersDark ? setDark() : setLight();
  } else if (mode === "light") {
    setLight();
  } else {
    setDark();
  }
};

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL;

/* ===========================================
   Helpers
   =========================================== */
const useDebounce = (value, delay = 400) => {
  const [v, setV] = useState(value);
  useEffect(() => {
    const id = setTimeout(() => setV(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return v;
};

const Progress = ({ value }) => (
  <div className="sd-progress">
    <div className="sd-progress__fill" style={{ width: `${value}%` }} />
  </div>
);

const computeSectionProgress = (items) => {
  const total = items.length;
  const completed = items.filter((x) => x.isCompleted || x.completed).length;
  return total === 0 ? 0 : Math.round((completed / total) * 100);
};

/* ===========================================
   Main Page
   =========================================== */
const StudentDashboard = () => {
  const { user, logout } = useAuth();

  const [content, setContent] = useState({ lectures: [], materials: [] });
  const [loading, setLoading] = useState(true);

  // settings
  const [openSettings, setOpenSettings] = useState(false);
  const [themeMode, setThemeMode] = useState(localStorage.getItem("sd_theme") || "dark"); // light | dark | auto
  const [density, setDensity] = useState(localStorage.getItem("sd_density") || "comfortable"); // comfortable | compact
  const [defaultSubject, setDefaultSubject] = useState(localStorage.getItem("sd_default_subject") || "");

  // filters
  const [subjectFilter, setSubjectFilter] = useState(defaultSubject || "");
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 400);

  // inject CSS & spinner once
  useEffect(() => {
    injectCSS();
    injectSpinner();
  }, []);

  // apply theme initial & on change
  useEffect(() => {
    applyTheme(themeMode);
  }, [themeMode]);

  // derive subject chips
  const subjectChips = useMemo(() => {
    const arr = [...content.lectures, ...content.materials]
      .map((x) => x.subject)
      .filter(Boolean);
    return Array.from(new Set(arr));
  }, [content]);

  const getSecureUrl = (id, type) => {
    const endpoint = type === "LECTURE" ? "lectures" : "materials";
    const action = type === "LECTURE" ? "stream" : "download";
    return `${BACKEND_URL}/api/${endpoint}/secure/${action}/${id}`;
  };

  const load = useCallback(
    async (subject = "") => {
      setLoading(true);
      try {
        const data = await contentService.getStudentContentAndProgress(subject);
        const normalize = (arr) =>
          arr.map((x) => ({
            ...x,
            isCompleted: x.isCompleted ?? x.completed ?? false,
          }));
        setContent({
          lectures: normalize(data.lectures),
          materials: normalize(data.materials),
        });
      } catch (err) {
        console.error("Load error:", err);
        if (err?.response?.status === 401 || err?.response?.status === 403) {
          toast.error("Session expired. Please login again.");
          logout();
        } else {
          toast.error("Failed to load content.");
        }
      } finally {
        setLoading(false);
      }
    },
    [logout]
  );

  // initial + on filter/search
  useEffect(() => {
    const query = (subjectFilter || debouncedSearch || "").trim();
    load(query);
  }, [load, subjectFilter, debouncedSearch]);

  const handleMarkComplete = async (id, type) => {
    // optimistic update
    setContent((prev) => ({
      lectures:
        type === "LECTURE"
          ? prev.lectures.map((i) => (i.id === id ? { ...i, isCompleted: true } : i))
          : prev.lectures,
      materials:
        type === "MATERIAL"
          ? prev.materials.map((i) => (i.id === id ? { ...i, isCompleted: true } : i))
          : prev.materials,
    }));

    try {
      await contentService.markComplete(id, type);
      toast.success("Marked as completed");
    } catch {
      toast.error("Could not update status");
      // revert
      setContent((prev) => ({
        lectures:
          type === "LECTURE"
            ? prev.lectures.map((i) => (i.id === id ? { ...i, isCompleted: false } : i))
            : prev.lectures,
        materials:
          type === "MATERIAL"
            ? prev.materials.map((i) => (i.id === id ? { ...i, isCompleted: false } : i))
            : prev.materials,
      }));
    }
  };

  const lecturePercent = useMemo(
    () => computeSectionProgress(content.lectures),
    [content.lectures]
  );
  const materialPercent = useMemo(
    () => computeSectionProgress(content.materials),
    [content.materials]
  );

  // Settings handlers
  const saveSettings = () => {
    localStorage.setItem("sd_theme", themeMode);
    localStorage.setItem("sd_density", density);
    localStorage.setItem("sd_default_subject", defaultSubject);
    applyTheme(themeMode);
    setSubjectFilter(defaultSubject || "");
    toast.success("Settings saved");
    setOpenSettings(false);
  };

  const resetSettings = () => {
    setThemeMode("dark");
    setDensity("comfortable");
    setDefaultSubject("");
    localStorage.removeItem("sd_theme");
    localStorage.removeItem("sd_density");
    localStorage.removeItem("sd_default_subject");
    applyTheme("dark");
    setSubjectFilter("");
    toast.success("Settings reset");
  };

  return (
    <div className={`sd-page ${density === "compact" ? "sd-compact" : ""}`}>
      <div className="sd-container">
        {/* Header */}
        <div className="sd-header">
          <h1 className="sd-title">Welcome{user?.username ? `, ${user.username}` : ""}!</h1>
          <div style={{ display: "flex", gap: 8 }}>
            <button className="sd-btn" title="Settings" onClick={() => setOpenSettings(true)}>
              <FaSlidersH /> Settings
            </button>
            <button className="sd-btn sd-btn--danger" onClick={logout}>
              <FaPowerOff /> Logout
            </button>
          </div>
        </div>

        {/* Progress: Lectures & Materials */}
        <div className="sd-row" style={{ marginBottom: 12 }}>
          <div className="sd-card">
            <div className="sd-line">
              <span>Lectures Progress</span>
              <span>{lecturePercent}%</span>
            </div>
            <Progress value={lecturePercent} />
          </div>

          <div className="sd-card">
            <div className="sd-line">
              <span>Materials Progress</span>
              <span>{materialPercent}%</span>
            </div>
            <Progress value={materialPercent} />
          </div>
        </div>

        {/* Filters */}
        <div className="sd-card" style={{ marginBottom: 12 }}>
          <div className="sd-filterbar">
            <div className="sd-chips">
              <button
                className={`sd-chip ${!subjectFilter ? "sd-chip--active" : ""}`}
                onClick={() => setSubjectFilter("")}
              >
                All
              </button>
              {subjectChips.map((s) => (
                <button
                  key={s}
                  className={`sd-chip ${subjectFilter === s ? "sd-chip--active" : ""}`}
                  onClick={() => setSubjectFilter((cur) => (cur === s ? "" : s))}
                >
                  {s}
                </button>
              ))}
            </div>
            <input
              className="sd-input"
              placeholder="Search by subject..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>

        {/* Lectures */}
        <Section title={`Available Lectures (${content.lectures.length})`}>
          {loading ? (
            <SkeletonCards />
          ) : (
            <div className="sd-grid">
              {content.lectures.map((item) => (
                <ContentCard
                  key={`L-${item.id}`}
                  item={item}
                  type="LECTURE"
                  onComplete={handleMarkComplete}
                  getDownloadUrl={(id) => getSecureUrl(id, "LECTURE")}
                />
              ))}
            </div>
          )}
        </Section>

        {/* Materials */}
        <Section title={`Study Materials (${content.materials.length})`}>
          {loading ? (
            <SkeletonCards />
          ) : (
            <div className="sd-grid">
              {content.materials.map((item) => (
                <ContentCard
                  key={`M-${item.id}`}
                  item={item}
                  type="MATERIAL"
                  onComplete={handleMarkComplete}
                  getDownloadUrl={(id) => getSecureUrl(id, "MATERIAL")}
                />
              ))}
            </div>
          )}
        </Section>
      </div>

      {/* Full-page Settings Screen */}
      {openSettings && (
        <>
          <div className="sd-overlay" onClick={() => setOpenSettings(false)} />
          <div className="sd-settings">
            <div className="sd-settings__header">
              <button className="sd-btn" onClick={() => setOpenSettings(false)}>
                <FaChevronLeft /> Back
              </button>
              <h2 style={{ margin: 0 }}>Settings</h2>
              <div style={{ marginLeft: "auto" }}>
                <button className="sd-btn" onClick={resetSettings}>
                  Reset
                </button>
              </div>
            </div>

            <div className="sd-settings__content">
              {/* Theme */}
              <div className="sd-card" style={{ marginBottom: 12 }}>
                <h3 style={{ marginTop: 0 }}>Theme</h3>
                <div className="sd-radio-row">
                  <label>
                    <input
                      className="sd-radio"
                      type="radio"
                      name="theme"
                      value="dark"
                      checked={themeMode === "dark"}
                      onChange={(e) => setThemeMode(e.target.value)}
                    />{" "}
                    Dark
                  </label>
                  <label>
                    <input
                      className="sd-radio"
                      type="radio"
                      name="theme"
                      value="light"
                      checked={themeMode === "light"}
                      onChange={(e) => setThemeMode(e.target.value)}
                    />{" "}
                    Light
                  </label>
                  <label>
                    <input
                      className="sd-radio"
                      type="radio"
                      name="theme"
                      value="auto"
                      checked={themeMode === "auto"}
                      onChange={(e) => setThemeMode(e.target.value)}
                    />{" "}
                    Auto (match system)
                  </label>
                </div>
              </div>

              {/* Density */}
              <div className="sd-card" style={{ marginBottom: 12 }}>
                <h3 style={{ marginTop: 0 }}>Density</h3>
                <div className="sd-radio-row">
                  <label>
                    <input
                      className="sd-radio"
                      type="radio"
                      name="density"
                      value="comfortable"
                      checked={density === "comfortable"}
                      onChange={(e) => setDensity(e.target.value)}
                    />{" "}
                    Comfortable
                  </label>
                  <label>
                    <input
                      className="sd-radio"
                      type="radio"
                      name="density"
                      value="compact"
                      checked={density === "compact"}
                      onChange={(e) => setDensity(e.target.value)}
                    />{" "}
                    Compact
                  </label>
                </div>
              </div>

              {/* Default Subject */}
              <div className="sd-card">
                <h3 style={{ marginTop: 0 }}>Default Subject Filter</h3>
                <p style={{ marginTop: 0, opacity: 0.8 }}>
                  When you open the dashboard, we’ll auto-apply this subject if available.
                </p>
                <input
                  className="sd-input sd-settings__input"
                  placeholder="e.g., Physics"
                  value={defaultSubject}
                  onChange={(e) => setDefaultSubject(e.target.value)}
                />
              </div>
            </div>

            <div className="sd-settings__footer">
              <button className="sd-btn" onClick={() => setOpenSettings(false)}>
                <FaTimes /> Cancel
              </button>
              <button className="sd-btn sd-btn--primary" onClick={saveSettings}>
                Save Settings
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

/* ===========================================
   Content Card
   =========================================== */
const ContentCard = ({ item, type, onComplete, getDownloadUrl }) => {
  const [loadingComplete, setLoadingComplete] = useState(false);
  const [loadingDownload, setLoadingDownload] = useState(false);

  const isCompleted = item.isCompleted || item.completed || false;
  const Icon = type === "LECTURE" ? FaVideo : FaBookOpen;

  const handleMarkComplete = async () => {
    if (isCompleted) return;
    setLoadingComplete(true);
    try {
      await onComplete(item.id, type);
    } finally {
      setLoadingComplete(false);
    }
  };

  const handleSecureAccess = async () => {
    const token = localStorage.getItem("user_token");
    if (!token) {
      toast.error("Not authenticated");
      return;
    }

    setLoadingDownload(true);
    try {
      const url = getDownloadUrl(item.id);
      const res = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        toast.error(`Failed: ${res.status}`);
        return;
      }

      const blob = await res.blob();
      const disp = res.headers.get("Content-Disposition");
      const match = disp?.match(/filename="(.+)"/);
      const filename =
        match?.[1] || `${item.title || (type === "MATERIAL" ? "notes" : "video")}`;

      const blobUrl = URL.createObjectURL(blob);

      if (type === "LECTURE") {
        window.open(blobUrl, "_blank");
      } else {
        const a = document.createElement("a");
        a.href = blobUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(blobUrl);
      }
    } catch {
      toast.error("Error accessing content");
    } finally {
      setLoadingDownload(false);
    }
  };

  return (
    <div className="sd-card">
      <div className="sd-card__header">
        <Icon color="var(--accent)" size={18} />
        <h3 style={{ margin: 0, color: "var(--fg)" }}>{item.title}</h3>
      </div>

      <p style={{ marginTop: 0 }}>
        Subject: <strong>{item.subject || "-"}</strong>
      </p>

      <div className="sd-card__footer">
        {isCompleted ? (
          <span className="sd-pill">
            <FaCheckCircle /> Completed
          </span>
        ) : (
          <button
            onClick={handleMarkComplete}
            disabled={loadingComplete}
            className="sd-btn sd-btn--success"
          >
            {loadingComplete ? (
              <>
                <FaSpinner className="spin" /> Updating…
              </>
            ) : (
              "Mark Complete"
            )}
          </button>
        )}

        <button
          onClick={handleSecureAccess}
          disabled={loadingDownload}
          className="sd-btn sd-btn--primary"
        >
          {loadingDownload ? (
            <>
              <FaSpinner className="spin" /> Loading…
            </>
          ) : (
            <>
              <FaDownload /> {type === "LECTURE" ? "View" : "Download"}
            </>
          )}
        </button>
      </div>
    </div>
  );
};

/* ===========================================
   Section + Skeleton
   =========================================== */
const Section = ({ title, children }) => (
  <div className="sd-container" style={{ paddingTop: 0 }}>
    <div className="sd-card">
      <div style={{ padding: "10px 10px 0" }}>
        <h2 style={{ margin: 0 }}>{title}</h2>
      </div>
      <div style={{ padding: 10 }}>{children}</div>
    </div>
  </div>
);

const SkeletonCards = () => (
  <div className="sd-grid">
    {[...Array(2)].map((_, i) => (
      <div key={i} className="sd-card">
        <div style={{ height: 18, width: "35%", background: "#2a2f3a", borderRadius: 6, marginBottom: 10 }} />
        <div style={{ height: 12, width: "65%", background: "#2a2f3a", borderRadius: 6, marginBottom: 10 }} />
        <div style={{ height: 36, width: "100%", background: "#2a2f3a", borderRadius: 8 }} />
      </div>
    ))}
  </div>
);

export default StudentDashboard;

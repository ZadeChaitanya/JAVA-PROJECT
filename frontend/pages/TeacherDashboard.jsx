// src/pages/TeacherDashboard.jsx

import React from "react";
import { useAuth } from "../context/AuthContext";
import { Link, Outlet, useLocation } from "react-router-dom";
import { FaChalkboardTeacher, FaVideo, FaBook, FaList, FaSignOutAlt } from "react-icons/fa";

const TeacherDashboard = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => location.pathname.includes(path);

  return (
    <div style={styles.wrapper}>
      {/* HEADER */}
      <header style={styles.header}>
        <div style={styles.headerLeft}>
          <FaChalkboardTeacher size={30} color="#0d6efd" />
          <h1 style={styles.title}>
            Teacher Portal — {user?.username || "Professor"}
          </h1>
        </div>

        <button onClick={logout} style={styles.logoutButton}>
          <FaSignOutAlt /> Logout
        </button>
      </header>

      {/* NAVIGATION */}
      <nav style={styles.nav}>
        <Link
          to="upload/video"
          style={{
            ...styles.navLink,
            ...(isActive("upload/video") ? styles.activeTab : {}),
          }}
        >
          <FaVideo /> Upload Lecture
        </Link>

        <Link
          to="upload/material"
          style={{
            ...styles.navLink,
            ...(isActive("upload/material") ? styles.activeTab : {}),
          }}
        >
          <FaBook /> Upload Material
        </Link>

        <Link
          to="my-uploads"
          style={{
            ...styles.navLink,
            ...(isActive("my-uploads") ? styles.activeTab : {}),
          }}
        >
          <FaList /> My Uploads
        </Link>

        <Link to="progress" style={styles.navLink}>Students Progress</Link>

        <Link
            to="attendance"
            style={{
              ...styles.navLink,
              ...(isActive("attendance") ? styles.activeTab : {}),
            }}
          >
            🗓️ Upload Attendance
        </Link>
          <Link
            to="view-attendance"
            style={{
                ...styles.navLink,
                ...(isActive("view-attendance") ? styles.activeTab : {}),
              }}
            >
    📊 View Attendance
</Link>

        
      </nav>

      {/* CONTENT AREA (ROUTES RENDER HERE) */}
      <div style={styles.contentContainer}>
        <Outlet />
      </div>
    </div>
  );
};

export default TeacherDashboard;

/* ✅ MODERN, RESPONSIVE, DARK-MODE FRIENDLY STYLES */
const styles = {
  wrapper: {
    maxWidth: "1100px",
    margin: "auto",
    padding: "20px",
    color: "var(--text-color)",
  },

  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    paddingBottom: "15px",
    borderBottom: "2px solid var(--border-color)",
  },

  headerLeft: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
  },

  title: {
    margin: 0,
    fontSize: "22px",
    fontWeight: "bold",
  },

  logoutButton: {
    backgroundColor: "#dc3545",
    border: "none",
    padding: "8px 14px",
    color: "#fff",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "bold",
    display: "flex",
    alignItems: "center",
    gap: "6px",
  },

  nav: {
    display: "flex",
    flexWrap: "wrap",
    gap: "12px",
    marginTop: "20px",
    paddingBottom: "15px",
    borderBottom: "1px solid var(--border-color)",
  },

  navLink: {
    textDecoration: "none",
    padding: "10px 15px",
    borderRadius: "6px",
    fontWeight: "600",
    display: "flex",
    alignItems: "center",
    gap: "6px",
    transition: "0.2s",
    background: "var(--button-bg)",
    color: "var(--button-text)",
  },

  activeTab: {
    background: "#0d6efd",
    color: "white",
    boxShadow: "0 2px 6px rgba(0,0,0,0.15)",
  },

  contentContainer: {
    marginTop: "25px",
    padding: "20px",
    background: "var(--card-bg)",
    borderRadius: "10px",
    boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
    minHeight: "450px",
  },
};

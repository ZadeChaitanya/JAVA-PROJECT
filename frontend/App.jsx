// src/App.jsx

import { Toaster } from 'react-hot-toast';
import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  Outlet,
} from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Login from "./components/Login";

// Dashboards
import StudentDashboard from "./pages/StudentDashboard";
import TeacherDashboard from "./pages/TeacherDashboard";
import AdminDashboard from "./pages/AdminDashboard";

// Upload
import UploadForm from "./components/UploadForm";
import QuizPage from "./pages/QuizPage";
import TeacherUploadsList from "./components/TeacherUploadsList";
import TeacherProgress from './pages/TeacherProgress';
import TeacherAttendance from "./pages/TeacherAttendance";
import ViewAttendance from "./pages/ViewAttendance";

// Protected Route Component
const ProtectedRoute = ({ allowedRoles }) => {
  const { isLoggedIn, user } = useAuth();

  if (!isLoggedIn) return <Navigate to="/" replace />;

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
};

const AppRoutes = () => {
  const { isLoggedIn, user } = useAuth();

  const getDashboardPath = () => {
    if (!user) return "/";
    switch (user.role) {
      case "ROLE_STUDENT":
        return "/student/dashboard";
      case "ROLE_TEACHER":
        return "/teacher/dashboard";
      case "ROLE_ADMIN":
        return "/admin/dashboard";
      default:
        return "/";
    }
  };

  return (
    <Routes>
      {/* Login */}
      <Route
        path="/"
        element={
          isLoggedIn ? <Navigate to={getDashboardPath()} /> : <Login />
        }
      />

      {/* Student Routes */}
      <Route element={<ProtectedRoute allowedRoles={["ROLE_STUDENT"]} />}>
        <Route path="/student/dashboard" element={<StudentDashboard />} />
        <Route path="/student/quiz/:testId" element={<ProtectedRoute element={<QuizPage/>} />} />
      </Route>

      {/* Teacher Routes */}
      <Route element={<ProtectedRoute allowedRoles={["ROLE_TEACHER"]} />}>
        <Route path="/teacher/dashboard" element={<TeacherDashboard />}>
          <Route
            index
            element={
              <div style={{ padding: "20px" }}>
                Select an action from the navigation bar above.
              </div>
            }
          />
          <Route
            path="upload/video"
            element={<UploadForm fileType="video" />}
          />
          <Route
            path="upload/material"
            element={<UploadForm fileType="material" />}
          />
          <Route 
            path="my-uploads" 
            element={<TeacherUploadsList/>} 
        />
          <Route path="progress" element={<TeacherProgress />} />

          <Route path="attendance" element={<TeacherAttendance />} />

          <Route path="view-attendance" element={<ViewAttendance />} />

        </Route>
      </Route>
      

      {/* Admin Routes */}
      <Route element={<ProtectedRoute allowedRoles={["ROLE_ADMIN"]} />}>
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>

      {/* ✅ Global toaster for notifications */}
      <Toaster position="top-right" reverseOrder={false} toastOptions={{
    style: {
      background: "var(--card)",
      color: "var(--fg)"
    }
  }} />
    </Router>
  );
}

export default App;

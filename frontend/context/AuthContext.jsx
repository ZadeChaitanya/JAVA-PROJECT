import React, { createContext, useState, useEffect, useContext, useMemo } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = authService.getCurrentUserToken();
    const role = localStorage.getItem('user_role');
    const id = localStorage.getItem('user_id');

    if (token) {
      setUser({ token, role, id });
      setIsLoggedIn(true);
    }
  }, []);

  const login = async (email, password) => {
    const userData = await authService.login(email, password);
    setUser(userData);
    setIsLoggedIn(true);
    return userData;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setIsLoggedIn(false);
  };

  const value = useMemo(
    () => ({ user, isLoggedIn, login, logout }),
    [user, isLoggedIn]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

export default AuthContext;
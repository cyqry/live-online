import { useContext, createContext, useState } from "react";
import Entry from "./Entry";
import { useCallback } from "react";

const NotificationContext = createContext();

export const useNotification = () => {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error('useNotification 必须在 NotificationProvider 下');
    }
    return context;
};

export const NotificationProvider = ({ children }) => {
    const [notification, setNotification] = useState({
        message: '',
        status: 'success',
        duration: 3000,
        key: 0
    });
    const showNotification = useCallback((message, status = "error", duration = 2000) => {
        setNotification(notification => ({
            message,
            status,
            duration,
            key: notification.key + 1
        }));
    }, []);

    return (
        <NotificationContext.Provider value={{ showNotification, show: showNotification }}>
            {children}
            <Entry
                message={notification.message}
                status={notification.status}
                duration={notification.duration}
                barKey={notification.key}
            />
        </NotificationContext.Provider>
    );
};
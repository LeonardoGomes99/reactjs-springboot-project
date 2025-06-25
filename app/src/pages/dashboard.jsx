import { Fragment, useEffect } from "react"
import { logout, refresh, validate } from "../utils/request";
import { useLocation, useNavigate } from "react-router-dom";

const useAuthValidation = () => {
    const navigate = useNavigate();
    const validateUser = async () => {
        try {
            const response = await validate();
            if (response.status !== 200) {
                navigate("/");
            }
        } catch (error) {
            navigate("/");
        }
    };

    useEffect(() => {
        validateUser();
    }, []);
};

export const Dashboard = () => {
    const navigate = useNavigate();
    const location = useLocation();
    useAuthValidation();
    const logoutUser = async () => {
        await logout();
        navigate("/");
    }
    const refreshUser = async () => {
        await refresh();
        navigate(0);
    }
    return (
        <Fragment>
            <h1>Dashboard</h1>
            <button onClick={logoutUser}>Deslogar</button>
            <button onClick={refreshUser}>Refresh</button>
        </Fragment>
    );
};
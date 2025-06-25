import { Fragment, useEffect, useState } from "react"
import { login, logout, validate } from "../utils/request";
import { useNavigate } from "react-router-dom";

export const Home = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        senha: '',
    });
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const response = await login(formData.email, formData.senha);
        if(response.status === 200){
            navigate('/dashboard');
        }
    };
    const validateUser = async () => {
            const response = await validate();
            if(response.status === 200){
                navigate("/dashboard")
            }else{
                await logout();
            }
        }
    useEffect(() => {        
        validateUser();
    },[])

    return (
        <Fragment>
            <div style={{ maxWidth: 400, margin: '0 auto', padding: 20 }}>
                <h2>Login</h2>
                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: 10 }}>
                        <label>Email:</label><br />
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <label>Senha:</label><br />
                        <input
                            type="password"
                            name="senha"
                            value={formData.senha}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <button type="submit">Entrar</button>
                </form>
            </div>
        </Fragment>
    )
}
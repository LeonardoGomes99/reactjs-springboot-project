
export const url = 'http://localhost:8080';

// Fazer login e armazenar cookie HTTPOnly
export const login = async (email, password) => {
  const response = await fetch(`${url}/api/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({
      username: email,
      password: password
    })
  });
  if (response.ok) {
    return { data: "", status: 200 }
  } else {
    console.error("Erro no login:", response.status);
  }
}

export const refresh = async () => {
  const response = await fetch(`${url}/api/refresh`, {
    method: "POST",
    credentials: "include",
  });
  if (response.ok) {    
    return { data: "", status: response.status };
  } else {
    return { data: "", status: 401 }
  }
}

// Fazer uma requisição autenticada após o login
export const validate = async () => {
  const response = await fetch(`${url}/api/validate`, {
    method: "POST",
    credentials: "include",
  });
  if (response.ok) {
    const data = await response.json();
    return { data: data, status: response.status };
  } else {
    return { data: "", status: 401 }
  }
}

// Fazer uma requisição autenticada para remover os tokens
export const logout = async () => {
  const response = await fetch(`${url}/api/logout`, {
    method: "POST",
    credentials: "include", // inclui os cookies HttpOnly na requisição
  });
  if (response.ok) {
    // Como 204 No Content não retorna JSON, não devemos chamar response.json()
    return { data: null, status: response.status };
  } else {
    return { data: "", status: response.status };
  }
};

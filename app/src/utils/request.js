
export const url = 'http://localhost:8080';
export const clientId = "meu-sistema";
export const clientSecret = "segredo-supersecreto";

// Fazer login e armazenar cookie HTTPOnly
export const login = async (email, password) => {
  const basicAuth = btoa(`${clientId}:${clientSecret}`);
  const response = await fetch(`${url}/api/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Basic ${basicAuth}`
    },
    credentials: "include",
    body: JSON.stringify({
      username: email,
      password: password
    })
  });

  if (response.ok) {
    return { data: "", status: 200 };
  } else {
    console.error("Erro no login:", response.status);
  }
};


// Fazer uma requisição caso o refresh token esteja inválido
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
    credentials: "include",
  });
  if (response.ok) {
    // Como 204 No Content não retorna JSON
    return { data: null, status: response.status };
  } else {
    return { data: "", status: response.status };
  }
};

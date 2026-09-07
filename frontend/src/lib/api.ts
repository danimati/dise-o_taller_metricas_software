export type Rol = 'CLIENTE' | 'EMPLEADO';

export type Comida = {
	id: number;
	nombre: string;
	precio: number;
	imagenURL?: string | null;
};

export type Configuracion = {
	id: number;
	ingrediente: {
		id: number;
		nombreIngrediente: string;
		unidadMedida: string;
	};
	cantidadBase: number;
	incluidoPorDefecto: boolean;
	permiteAdicional: boolean;
	permiteRetirar: boolean;
	precioAdicional: number;
};

export type ComidaDetalle = Comida & { ingredientes: Configuracion[] };

export type Ingrediente = {
	id: number;
	nombreIngrediente: string;
	unidadMedida: string;
	stockActual: number;
};

export type AuthResponse = {
	token: string;
	nombreUsuario: string;
	rol: Rol;
	idCliente: number | null;
	nombre: string | null;
	documento: string | null;
	email: string | null;
};

export type Pedido = {
	detalles: Array<{
		comidaId: number;
		cantidad: number;
		personalizaciones: Array<{
			configuracionId: number;
			tipoAdicion: 'ADICIONAL' | 'RETIRADO';
			cantidad: number;
		}>;
	}>;
};

export type ErrorResponse = {
	status?: number;
	error?: string;
	message?: string;
};

const baseUrl = 'http://localhost:8080';

function getToken() {
	return typeof localStorage === 'undefined' ? null : localStorage.getItem('gfc_token');
}

export function saveSession(response: AuthResponse) {
	localStorage.setItem('gfc_token', response.token);
	localStorage.setItem('gfc_role', response.rol);
	localStorage.setItem('gfc_user', JSON.stringify(response));
}

export function clearSession() {
	localStorage.removeItem('gfc_token');
	localStorage.removeItem('gfc_role');
	localStorage.removeItem('gfc_user');
}

export function getSession() {
	if (typeof localStorage === 'undefined') return null;
	const user = localStorage.getItem('gfc_user');
	return user ? (JSON.parse(user) as AuthResponse) : null;
}

async function request<T>(path: string, options: RequestInit = {}) {
	const headers = new Headers(options.headers);
	headers.set('Content-Type', 'application/json');
	const token = getToken();
	if (token) headers.set('Authorization', `Bearer ${token}`);
	const response = await fetch(`${baseUrl}${path}`, { ...options, headers });
	if (!response.ok) {
		const error = (await response.json().catch(() => ({}))) as ErrorResponse;
		if (response.status === 401) clearSession();
		throw new Error(error.message || error.error || 'No pudimos completar la solicitud.');
	}
	if (response.status === 204) return undefined as T;
	return (await response.json()) as T;
}

export const api = {
	getComidas: () => request<Comida[]>('/api/comidas'),
	getComida: (id: number) => request<ComidaDetalle>(`/api/comidas/${id}`),
	login: (body: { identificador: string; contrasena: string }) => request<AuthResponse>('/api/auth/login', { method: 'POST', body: JSON.stringify(body) }),
	register: (body: Record<string, string>) => request<AuthResponse>('/api/auth/registro', { method: 'POST', body: JSON.stringify(body) }),
	createOrder: (body: Pedido) => request<{ valorTotal?: number; total?: number; id?: number }>('/api/pedidos', { method: 'POST', body: JSON.stringify(body) }),
	getIngredientes: () => request<Ingrediente[]>('/api/ingredientes'),
	addInventory: (id: number, body: { cantidad: number; motivo: string; fecha?: string }) => request(`/api/ingredientes/${id}/entradas`, { method: 'POST', body: JSON.stringify(body) }),
	removeInventory: (id: number, body: { cantidad: number; motivo: string; fecha?: string }) => request(`/api/ingredientes/${id}/salidas`, { method: 'POST', body: JSON.stringify(body) }),
	getTopComidas: async () => [{ nombre: 'Combo GFC Crunch', pedidos: 38 }, { nombre: 'Alitas Picantes', pedidos: 29 }, { nombre: 'Papas Kimchi', pedidos: 21 }],
};
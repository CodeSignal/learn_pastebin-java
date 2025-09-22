export const API_BASE_URL = '';
export const API_ENDPOINTS = {
  LOGIN: `/api/auth/login`,
  REGISTER: `/api/auth/register`,
  SNIPPETS: `/api/snippets`,
  SNIPPET: (id: string) => `/api/snippets/${id}`,
};
export const JWT_SECRET_KEY = 'jwt-secret-key';
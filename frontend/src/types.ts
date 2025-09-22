export interface Snippet {
  id?: string;
  title: string;
  content: string;
  language: string;
  userId: string;
}

export interface User {
  username: string;
  token: string;
}

export enum Role {
  USER,
  ADMIN,
}
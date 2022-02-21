export interface IFoo {
  id?: string;
  surname?: string | null;
  name?: string | null;
}

export const defaultValue: Readonly<IFoo> = {};

import React, { useState, use } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { UserContext } from "@/context/UserContext";
import { Link, useNavigate } from "react-router";
import { toast } from "react-toastify";

const LoginForm = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const { login } = use(UserContext);
    const navigate = useNavigate();

    const BASE_URL = import.meta.env.VITE_API_URL_AUTH;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const resp = await fetch(`${BASE_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: email,
                    password: password,
                }),
            });

            if (!resp.ok) throw new Error("Error en la autenticación");

            const data = await resp.json();
            login(data);
            toast.success("Usuario autenticado");
            navigate("/");
        } catch (error) {
            console.error(error);
            toast.error("usuario o contraseña incorrecta");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-50 p-4">
            <Card className="w-full max-w-md shadow-md">
                <CardHeader>
                    <CardTitle className="text-center text-2xl">Iniciar Sesión</CardTitle>
                    <CardDescription className="text-center">
                        Ingresa tu usuario y contraseña para acceder
                    </CardDescription>
                </CardHeader>

                <form onSubmit={handleSubmit}>
                    <CardContent className="space-y-6">
                        <div className="space-y-2">
                            <Label htmlFor="email">Usuario</Label>
                            <Input
                                id="email"
                                type="text"
                                placeholder="usuario o email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                disabled={isLoading}
                            />
                        </div>
                        <div className="space-y-2">
                            <div className="flex items-center justify-between">
                                <Label htmlFor="password">Contraseña</Label>
                            </div>
                            <Input
                                id="password"
                                type="password"
                                placeholder="contraseña..."
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                disabled={isLoading}
                            />
                        </div>
                    </CardContent>

                    <CardFooter className="flex flex-col gap-4">
                        <Button type="submit" className="w-full mt-4" disabled={isLoading}>
                            {isLoading ? "Iniciando sesión..." : "Iniciar Sesión"}
                        </Button>
                        <p className="text-center text-sm text-muted-foreground">
                            ¿No tienes cuenta?{" "}
                            <Link to="/register" className="font-medium text-foreground hover:underline">
                                Regístrate
                            </Link>
                        </p>
                    </CardFooter>
                </form>
            </Card>
        </div>
    );
};

export default LoginForm;

"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { User, Mail, Lock, Eye, EyeOff } from "lucide-react"
import { Link } from "react-router"
import { toast } from "react-toastify"

export default function RegisterPage() {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
    })

    const BASE_URL = import.meta.env.VITE_API_URL_AUTH;

    const [showPassword, setShowPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [errors, setErrors] = useState<Record<string, string>>({})

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setFormData((prev) => ({ ...prev, [name]: value }))
        if (errors[name]) {
            setErrors((prev) => ({ ...prev, [name]: "" }))
        }
    }

    const validateForm = () => {
        const newErrors: Record<string, string> = {}

        if (!formData.username.trim()) {
            newErrors.username = "El nombre de usuario es requerido"
        } else if (formData.username.length < 8) {
            newErrors.username = "El nombre de usuario debe tener al menos 8 caracteres"
        }

        if (!formData.email.trim()) {
            newErrors.email = "El correo electrónico es requerido"
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = "El correo electrónico no es válido"
        }

        if (!formData.password) {
            newErrors.password = "La contraseña es requerida"
        } else if (formData.password.length < 6) {
            newErrors.password = "La contraseña debe tener al menos 6 caracteres"
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = "Debes confirmar tu contraseña"
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Las contraseñas no coinciden"
        }

        setErrors(newErrors)
        return Object.keys(newErrors).length === 0
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (validateForm()) {
            try {
                const response = await fetch(`${BASE_URL}/register`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: formData.username,
                        password: formData.password,
                        email: formData.email,
                        role: 'USER'
                    })
                });

                if (!response.ok) {
                    throw new Error('Error al registrar el usuario');
                    toast.error('Error al registrar el usuario, vuelva a intentarlo')
                }

                toast.success('Usuario registrado correctamente')

                setFormData({
                    username: '',
                    email: '',
                    password: '',
                    confirmPassword: ''
                });

            } catch (error) {
                console.error('Error en la solicitud:', error);
            }
        }
    };


    return (
        <div className="min-h-screen bg-background">
            <main className="container mx-auto px-4 py-12">
                <div className="max-w-md mx-auto">
                    <Card className="shadow-lg">
                        <CardHeader className="space-y-1">
                            <CardTitle className="text-2xl font-bold text-center">Crear cuenta</CardTitle>
                            <CardDescription className="text-center">Ingresa tus datos para registrarte</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                {/* Username Field */}
                                <div className="space-y-2">
                                    <Label htmlFor="username">Nombre de usuario</Label>
                                    <div className="relative">
                                        <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                                        <Input
                                            id="username"
                                            name="username"
                                            type="text"
                                            placeholder="Tu nombre de usuario"
                                            value={formData.username}
                                            onChange={handleChange}
                                            className={`pl-10 ${errors.username ? "border-red-500" : ""}`}
                                        />
                                    </div>
                                    {errors.username && <p className="text-sm text-red-500">{errors.username}</p>}
                                </div>

                                {/* Email Field */}
                                <div className="space-y-2">
                                    <Label htmlFor="email">Correo electrónico</Label>
                                    <div className="relative">
                                        <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                                        <Input
                                            id="email"
                                            name="email"
                                            type="email"
                                            placeholder="tu@email.com"
                                            value={formData.email}
                                            onChange={handleChange}
                                            className={`pl-10 ${errors.email ? "border-red-500" : ""}`}
                                        />
                                    </div>
                                    {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
                                </div>

                                {/* Password Field */}
                                <div className="space-y-2">
                                    <Label htmlFor="password">Contraseña</Label>
                                    <div className="relative">
                                        <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                                        <Input
                                            id="password"
                                            name="password"
                                            type={showPassword ? "text" : "password"}
                                            placeholder="••••••••"
                                            value={formData.password}
                                            onChange={handleChange}
                                            className={`pl-10 pr-10 ${errors.password ? "border-red-500" : ""}`}
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPassword(!showPassword)}
                                            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        >
                                            {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                                        </button>
                                    </div>
                                    {errors.password && <p className="text-sm text-red-500">{errors.password}</p>}
                                </div>

                                {/* Confirm Password Field */}
                                <div className="space-y-2">
                                    <Label htmlFor="confirmPassword">Confirmar contraseña</Label>
                                    <div className="relative">
                                        <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                                        <Input
                                            id="confirmPassword"
                                            name="confirmPassword"
                                            type={showConfirmPassword ? "text" : "password"}
                                            placeholder="••••••••"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            className={`pl-10 pr-10 ${errors.confirmPassword ? "border-red-500" : ""}`}
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        >
                                            {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                                        </button>
                                    </div>
                                    {errors.confirmPassword && <p className="text-sm text-red-500">{errors.confirmPassword}</p>}
                                </div>

                                <Button type="submit" className="w-full" size="lg">
                                    Registrarse
                                </Button>
                            </form>

                            <div className="mt-6 text-center text-sm">
                                <span className="text-muted-foreground">¿Ya tienes una cuenta? </span>
                                <Link to="/login" className="text-primary font-medium hover:underline">
                                    Inicia sesión
                                </Link>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </main>
        </div>
    )
}

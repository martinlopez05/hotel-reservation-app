import React from 'react'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card'
import { Label } from '@radix-ui/react-label'
import { Input } from '../components/ui/input'
import { Button } from '../components/ui/button'

const CustomLogin = () => {
    return (
        <>
            {/* Login Form */}
            {

                <div className="bg-muted border-b">
                    <div className="container mx-auto px-4 py-6">
                        <Card className="max-w-md mx-auto">
                            <CardHeader>
                                <CardTitle>Iniciar Sesión</CardTitle>
                                <CardDescription>Ingresa tus credenciales para continuar</CardDescription>
                            </CardHeader>
                            <form>
                                <CardContent className="space-y-4">
                                    <div className="space-y-2">
                                        <Label htmlFor="email">Email</Label>
                                        <Input
                                            id="email"
                                            type="email"
                                            placeholder="tu@email.com"

                                            required
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="password">Contraseña</Label>
                                        <Input
                                            id="password"
                                            type="password"
                                            placeholder="••••••••"
                                            required
                                        />
                                    </div>
                                    <p className="text-xs text-muted-foreground">
                                        Demo: admin@hotel.com / admin123 o user@hotel.com / user123
                                    </p>
                                </CardContent>
                                <CardFooter className="flex gap-2">
                                    <Button type="submit" className="flex-1">
                                        Ingresar
                                    </Button>
                                    <Button type="button" variant="outline">
                                        Cancelar
                                    </Button>
                                </CardFooter>
                            </form>
                        </Card>
                    </div>
                </div>
            }
        </>

    )
}

export default CustomLogin

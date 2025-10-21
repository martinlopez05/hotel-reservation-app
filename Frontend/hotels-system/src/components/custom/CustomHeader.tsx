import React from 'react'
import { Link } from 'react-router'
import { Button } from '../ui/button'

const CustomHeader = () => {
    return (
        <header className="border-b bg-card">
            <div className="container mx-auto px-4 py-4 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                        <span className="text-primary-foreground font-bold text-lg">H</span>
                    </div>
                    <h1 className="text-2xl font-bold text-foreground">HotelReserva</h1>
                </div>

                <nav className="flex items-center gap-4">
                    <span className="text-sm text-muted-foreground">Hola, martin</span>
                    <Link to="/mis-reservas">
                        <Button variant="outline">Mis Reservas</Button>
                    </Link>
                    {/*{user ? (
                        <>
                            <span className="text-sm text-muted-foreground">Hola, {user.name}</span>
                            <Link href="/mis-reservas">
                                <Button variant="outline">Mis Reservas</Button>
                            </Link>
                            {user.role === "admin" && (
                                <Link href="/admin">
                                    <Button variant="outline">Admin</Button>
                                </Link>
                            )}
                        </>
                    ) : (
                        <Button onClick={() => setShowLogin(!showLogin)}>Iniciar Sesión</Button>
                    )}*/}
                </nav>
            </div>
        </header>
    )
}

export default CustomHeader

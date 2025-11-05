import { useState, use } from "react"
import { Button } from "../ui/button"
import { Calendar, Menu, X } from "lucide-react"
import { UserContext } from "@/context/UserContext"
import { useNavigate } from "react-router"

interface HeaderProps {
    onReservationsClick?: () => void
}

const CustomHeader = ({ onReservationsClick }: HeaderProps) => {
    const { user, logout } = use(UserContext)
    const navigate = useNavigate()
    const [menuOpen, setMenuOpen] = useState(false)

    const handleLogout = () => {
        logout()
        navigate("/login")
        setMenuOpen(false)
    }

    const handleNavigate = (path: string) => {
        navigate(path)
        setMenuOpen(false)
    }

    return (
        <header className="border-b bg-card">
            <div className="container mx-auto px-4 py-4 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                        <span className="text-primary-foreground font-bold text-lg">H</span>
                    </div>
                    <h1 className="text-xl sm:text-2xl font-bold text-foreground">HotelFly</h1>
                    <a
                        href="/"
                        className="ml-3 hidden sm:block text-sm text-muted-foreground hover:text-primary transition-colors"
                    >
                        Inicio
                    </a>
                </div>

                <button
                    className="sm:hidden p-2 text-foreground hover:text-primary transition-colors"
                    onClick={() => setMenuOpen(!menuOpen)}
                >
                    {menuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
                </button>

                <nav className="hidden sm:flex items-center gap-4">
                    {user ? (
                        <>
                            <span className="text-sm text-muted-foreground">Hola, {user.username}</span>
                            <Button variant="outline" size="sm" onClick={handleLogout}>
                                Cerrar sesión
                            </Button>
                        </>
                    ) : (
                        <Button variant="default" size="sm" onClick={() => handleNavigate("/login")}>
                            Iniciar sesión
                        </Button>
                    )}

                    {user?.role === "ROLE_ADMIN" && (
                        <Button variant="default" size="sm" onClick={() => handleNavigate("/admin")}>
                            Administración
                        </Button>
                    )}

                    {onReservationsClick && (
                        <Button
                            onClick={onReservationsClick}
                            variant="default"
                            size="sm"
                            className="gap-2"
                        >
                            <Calendar className="w-4 h-4" />
                            Mis Reservas
                        </Button>
                    )}
                </nav>
            </div>

            {menuOpen && (
                <div className="sm:hidden bg-card border-t border-border px-4 py-3 space-y-3 animate-slide-down">
                    {user ? (
                        <>
                            <p className="text-sm text-muted-foreground">Hola, {user.username}</p>
                            <Button
                                variant="outline"
                                size="sm"
                                className="w-full"
                                onClick={handleLogout}
                            >
                                Cerrar sesión
                            </Button>
                        </>
                    ) : (
                        <Button
                            variant="default"
                            size="sm"
                            className="w-full"
                            onClick={() => handleNavigate("/login")}
                        >
                            Iniciar sesión
                        </Button>
                    )}

                    {user?.role === "ROLE_ADMIN" && (
                        <Button
                            variant="default"
                            size="sm"
                            className="w-full"
                            onClick={() => handleNavigate("/admin")}
                        >
                            Administración
                        </Button>
                    )}

                    {onReservationsClick && (
                        <Button
                            onClick={() => {
                                onReservationsClick()
                                setMenuOpen(false)
                            }}
                            variant="default"
                            size="sm"
                            className="w-full gap-2"
                        >
                            <Calendar className="w-4 h-4" />
                            Mis Reservas
                        </Button>
                    )}

                    <a
                        href="/"
                        className="block text-center text-sm text-muted-foreground hover:text-primary transition-colors"
                        onClick={() => setMenuOpen(false)}
                    >
                        Inicio
                    </a>
                </div>
            )}
        </header>
    )
}

export default CustomHeader


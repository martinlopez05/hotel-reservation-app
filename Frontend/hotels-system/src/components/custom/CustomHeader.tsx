import { Button } from '../ui/button'
import { Calendar } from 'lucide-react'
import { UserContext } from '@/context/UserContext'
import { use } from 'react'
import { useNavigate } from 'react-router'

interface HeaderProps {
    onReservationsClick?: () => void
}

const CustomHeader = ({ onReservationsClick }: HeaderProps) => {

    const { user, logout } = use(UserContext);

    const navigate = useNavigate();



    return (
        <header className="border-b bg-card">
            <div className="container mx-auto px-4 py-4 flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                        <span className="text-primary-foreground font-bold text-lg">H</span>
                    </div>
                    <h1 className="text-2xl font-bold text-foreground">Reservas de Hoteles</h1>
                </div>

                <nav className="flex items-center gap-4">
                    {user ? (
                        <>
                            <span className="text-sm text-muted-foreground">Hola, {user.username}</span>
                            <Button variant="outline" size="sm" onClick={() => {
                                logout();
                                navigate('/login');
                            }}>
                                Cerrar sesión
                            </Button>
                        </>
                    ) : (
                        <Button variant="default" size="sm" onClick={() => navigate('/login')}>
                            Iniciar sesión
                        </Button>
                    )}

                    {user?.role === "ADMIN" ? (
                        <Button
                            variant="default"
                            size="sm"
                            onClick={() => navigate('/admin')}
                        >
                            Administración
                        </Button>
                    ) : (
                        onReservationsClick && (
                            <Button
                                onClick={onReservationsClick}
                                variant="default"
                                size="sm"
                                className="gap-2"
                            >
                                <Calendar className="w-4 h-4" />
                                Mis Reservas
                            </Button>
                        )
                    )}
                </nav>

            </div >
        </header >
    )
}

export default CustomHeader

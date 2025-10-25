import { useState, useEffect, use } from "react"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { PlusIcon, PencilIcon, TrashIcon } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import type { Room } from "@/Hotels/data/room.interface"
import { DeleteDialog } from "./Delete-dialog"
import { RoomDialog } from "./Rooms-dialog"
import { roomApi } from "@/Hotels/roomApi"
import { toast } from "react-toastify"
import { UserContext } from "@/context/UserContext"

export function RoomsTable() {
    const [rooms, setRooms] = useState<Room[]>([])
    const [loading, setLoading] = useState(true)
    const [dialogOpen, setDialogOpen] = useState(false)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
    const [selectedRoom, setSelectedRoom] = useState<Room | null>(null)
    const [roomToDelete, setRoomToDelete] = useState<number | null>(null)
    useEffect(() => {
        fetchRooms()
    }, [])

    const { user } = use(UserContext);

    const fetchRooms = async () => {
        try {
            setLoading(true)
            const response = await roomApi.get('', {
                headers: {
                    Authorization: `Bearer ${user?.token}`,
                }
            })
            if (response) {
                const { data } = await response
                setRooms(data)
            }
        } catch (error) {
            toast.error('Error al cargar habitaciones')
        } finally {
            setLoading(false)
        }
    }


    const handleCreate = () => {
        setSelectedRoom(null)
        setDialogOpen(true)
    }

    const handleEdit = (room: Room) => {
        setSelectedRoom(room)
        setDialogOpen(true)
    }

    const handleDelete = (id: number) => {
        setRoomToDelete(id)
        setDeleteDialogOpen(true)
    }

    const confirmDelete = async () => {
        if (!roomToDelete) return

        try {
            await roomApi.delete(`/${roomToDelete}`, {
                headers: {
                    Authorization: `Bearer ${user?.token}`,
                }
            })
            toast.error('Habitacion eliminada correctamente')
        } catch (error) {
            toast.error('Error al eliminar habitación')
        }
    }

    const handleSuccess = () => {
        fetchRooms()
        setDialogOpen(false)
    }

    if (loading) {
        return <div className="text-center py-8">Cargando habitaciones...</div>
    }

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Habitaciones</CardTitle>
                    <Button onClick={handleCreate}>
                        <PlusIcon />
                        Nueva Habitación
                    </Button>
                </CardHeader>
                <CardContent>
                    <div className="rounded-md border">
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Número</TableHead>
                                    <TableHead>Hotel</TableHead>
                                    <TableHead>Capacidad</TableHead>
                                    <TableHead>Precio/Noche</TableHead>
                                    <TableHead>Rating</TableHead>
                                    <TableHead>Estado</TableHead>
                                    <TableHead className="text-right">Acciones</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {rooms.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                                            No hay habitaciones registradas
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    rooms.map((room) => (
                                        <TableRow key={room.id}>
                                            <TableCell className="font-medium">#{room.roomNumber}</TableCell>
                                            <TableCell>{room.hotelId || `Hotel ID: ${room.hotelId}`}</TableCell>
                                            <TableCell>{room.capacity} personas</TableCell>
                                            <TableCell>${room.pricePerNight}</TableCell>
                                            <TableCell>
                                                <div className="flex items-center gap-1">
                                                    <span>{room.rating}</span>
                                                    <span className="text-yellow-500">★</span>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <Badge variant={room.available ? "default" : "secondary"}>
                                                    {room.available ? "Disponible" : "Ocupada"}
                                                </Badge>
                                            </TableCell>
                                            <TableCell className="text-right">
                                                <div className="flex justify-end gap-2">
                                                    <Button variant="outline" size="icon-sm" onClick={() => handleEdit(room)}>
                                                        <PencilIcon />
                                                    </Button>
                                                    <Button variant="destructive" size="icon-sm" onClick={() => handleDelete(room.id)}>
                                                        <TrashIcon />
                                                    </Button>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </CardContent>
            </Card>

            <RoomDialog open={dialogOpen} onOpenChange={setDialogOpen} room={selectedRoom} onSuccess={handleSuccess} />

            <DeleteDialog
                open={deleteDialogOpen}
                onOpenChange={setDeleteDialogOpen}
                onConfirm={confirmDelete}
                title="Eliminar Habitación"
                description="¿Estás seguro de que deseas eliminar esta habitación? Esta acción no se puede deshacer."
            />
        </>
    )
}

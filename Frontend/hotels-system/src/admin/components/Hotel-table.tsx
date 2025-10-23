import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { PlusIcon, PencilIcon, TrashIcon } from "lucide-react"
import { HotelDialog } from "./Hotel-dialog"
import { DeleteDialog } from "./Delete-dialog"
import type { Hotel } from "@/Hotels/data/hotel.interface"
import { toast } from "react-toastify"


export function HotelsTable() {
    const [hotels, setHotels] = useState<Hotel[]>([])
    const [loading, setLoading] = useState(true)
    const [dialogOpen, setDialogOpen] = useState(false)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
    const [selectedHotel, setSelectedHotel] = useState<Hotel | null>(null)
    const [hotelToDelete, setHotelToDelete] = useState<number | null>(null)

    const BASE_URL_HOTEL = import.meta.env.VITE_API_URL_HOTEL;

    useEffect(() => {
        fetchHotels()
    }, [])

    const fetchHotels = async () => {
        try {
            setLoading(true)
            const response = await fetch(`${BASE_URL_HOTEL}/hotels`)
            if (response.ok) {
                const data = await response.json()
                setHotels(data)
            }
        } catch (error) {
            console.error("Error fetching hotels:", error)
        } finally {
            setLoading(false)
        }
    }

    const handleCreate = () => {
        setSelectedHotel(null)
        setDialogOpen(true)
    }

    const handleEdit = (hotel: Hotel) => {
        setSelectedHotel(hotel)
        setDialogOpen(true)
    }

    const handleDelete = (id: number) => {
        setHotelToDelete(id)
        setDeleteDialogOpen(true)
    }

    const confirmDelete = async () => {
        if (!hotelToDelete) return

        try {
            const response = await fetch(`${BASE_URL_HOTEL}/hotels/${hotelToDelete}`, {
                method: "DELETE",
            })

            if (response.ok) {
                await fetchHotels()
                setDeleteDialogOpen(false)
                setHotelToDelete(null)
                toast.success('Hotel eliminado correctamente')
            }
        } catch (error) {
            console.error("Error deleting hotel:", error)
        }
    }

    const handleSuccess = () => {
        fetchHotels()
        setDialogOpen(false)
    }

    if (loading) {
        return <div className="text-center py-8">Cargando hoteles...</div>
    }

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Hoteles</CardTitle>
                    <Button onClick={handleCreate}>
                        <PlusIcon />
                        Nuevo Hotel
                    </Button>
                </CardHeader>
                <CardContent>
                    <div className="rounded-md border">
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Nombre</TableHead>
                                    <TableHead>Ubicación</TableHead>
                                    <TableHead>País</TableHead>
                                    <TableHead>Rating</TableHead>
                                    <TableHead>Email</TableHead>
                                    <TableHead>Teléfono</TableHead>
                                    <TableHead className="text-right">Acciones</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {hotels.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                                            No hay hoteles registrados
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    hotels.map((hotel) => (
                                        <TableRow key={hotel.id}>
                                            <TableCell className="font-medium">{hotel.name}</TableCell>
                                            <TableCell>{hotel.location}</TableCell>
                                            <TableCell>{hotel.country}</TableCell>
                                            <TableCell>
                                                <div className="flex items-center gap-1">
                                                    <span>{hotel.rating}</span>
                                                    <span className="text-yellow-500">★</span>
                                                </div>
                                            </TableCell>
                                            <TableCell>{hotel.email}</TableCell>
                                            <TableCell>{hotel.phone}</TableCell>
                                            <TableCell className="text-right">
                                                <div className="flex justify-end gap-2">
                                                    <Button variant="outline" size="icon-sm" onClick={() => handleEdit(hotel)}>
                                                        <PencilIcon />
                                                    </Button>
                                                    <Button variant="destructive" size="icon-sm" onClick={() => handleDelete(hotel.id)}>
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

            <HotelDialog open={dialogOpen} onOpenChange={setDialogOpen} hotel={selectedHotel} onSuccess={handleSuccess} />

            <DeleteDialog
                open={deleteDialogOpen}
                onOpenChange={setDeleteDialogOpen}
                onConfirm={confirmDelete}
                title="Eliminar Hotel"
                description="¿Estás seguro de que deseas eliminar este hotel? Esta acción no se puede deshacer."
            />
        </>
    )
}

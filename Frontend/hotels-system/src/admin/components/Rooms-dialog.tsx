"use client"

import type React from "react"

import { useState, useEffect, use } from "react"
import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Checkbox } from "@/components/ui/checkbox"
import type { Hotel } from "@/Hotels/data/hotel.interface"
import type { Room } from "@/Hotels/data/room.interface"
import { hotelApi } from "@/Hotels/hotelApi"
import { toast } from "react-toastify"
import { UserContext } from "@/context/UserContext"


type RoomDialogProps = {
    open: boolean
    onOpenChange: (open: boolean) => void
    room: Room | null
    onSuccess: () => void
}

export function RoomDialog({ open, onOpenChange, room, onSuccess }: RoomDialogProps) {
    const [loading, setLoading] = useState(false)
    const [hotels, setHotels] = useState<Hotel[]>([])
    const [formData, setFormData] = useState({
        roomNumber: 0,
        hotelId: 0,
        capacity: 2,
        imageUrl: "",
        available: true,
        rating: 5,
        pricePerNight: 0,
        description: "",
    })

    const { user } = use(UserContext);

    useEffect(() => {
        if (open) {
            fetchHotels()
        }
    }, [open])

    useEffect(() => {
        if (room) {
            setFormData({
                roomNumber: room.roomNumber,
                hotelId: room.hotelId,
                capacity: room.capacity,
                imageUrl: room.imageUrl,
                available: room.available,
                rating: room.rating,
                pricePerNight: room.pricePerNight,
                description: room.description,
            })
        } else {
            setFormData({
                roomNumber: 0,
                hotelId: 0,
                capacity: 2,
                imageUrl: "",
                available: true,
                rating: 5,
                pricePerNight: 0,
                description: "",
            })
        }
    }, [room, open])

    const BASE_URL_ROOM = import.meta.env.VITE_API_URL_ROOMS;


    const fetchHotels = async () => {
        try {
            const response = await hotelApi.get('', {
                headers: {
                    Authorization: `Bearer ${user?.token}`,
                    "ngrok-skip-browser-warning": "true"
                },
            })
            const { data } = await response
            setHotels(data)

        } catch (error) {
            console.error("Error fetching hotels:", error)
        }
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)

        try {
            const url = room ? `${BASE_URL_ROOM}/${room.id}` : `${BASE_URL_ROOM}`
            const method = room ? "PUT" : "POST"

            const response = await fetch(url, {
                method,
                headers: {
                    "Authorization": `Bearer ${user ? user.token : 0}`,
                    "Content-Type": "application/json",
                    "ngrok-skip-browser-warning": "true"
                },
                body: JSON.stringify(formData),
            })

            if (response.ok) {
                if (method === 'POST') {
                    toast.success('Habitación creada correctamente')
                }
                if (method === 'PUT') {
                    toast.success('Habitación editada correctamente')
                }
                onSuccess()
            }
        } catch (error) {
            console.error("Error saving room:", error)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>{room ? "Editar Habitación" : "Nueva Habitación"}</DialogTitle>
                    <DialogDescription>
                        {room ? "Modifica los datos de la habitación" : "Completa los datos de la nueva habitación"}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="roomNumber">Número de Habitación *</Label>
                            <Input
                                id="roomNumber"
                                type="number"
                                value={formData.roomNumber}
                                onChange={(e) => setFormData({ ...formData, roomNumber: Number.parseInt(e.target.value) })}
                                required
                                min="1"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="hotelId">Hotel *</Label>
                            <Select
                                value={formData.hotelId.toString()}
                                onValueChange={(value) => setFormData({ ...formData, hotelId: Number.parseInt(value) })}
                                required
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Selecciona un hotel" />
                                </SelectTrigger>
                                <SelectContent>
                                    {hotels.map((hotel) => (
                                        <SelectItem key={hotel.id} value={hotel.id.toString()}>
                                            {hotel.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="capacity">Capacidad *</Label>
                            <Input
                                id="capacity"
                                type="number"
                                value={formData.capacity}
                                onChange={(e) => setFormData({ ...formData, capacity: Number.parseInt(e.target.value) })}
                                required
                                min="1"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="pricePerNight">Precio por Noche *</Label>
                            <Input
                                id="pricePerNight"
                                type="number"
                                step="0.01"
                                value={formData.pricePerNight}
                                onChange={(e) => setFormData({ ...formData, pricePerNight: Number.parseFloat(e.target.value) })}
                                required
                                min="0"
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="rating">Rating *</Label>
                            <Select
                                value={formData.rating.toString()}
                                onValueChange={(value) => setFormData({ ...formData, rating: Number.parseInt(value) })}
                            >
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    {[1, 2, 3, 4, 5].map((rating) => (
                                        <SelectItem key={rating} value={rating.toString()}>
                                            {rating} ★
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-2 flex items-end pb-2">
                            <div className="flex items-center gap-2">
                                <Checkbox
                                    id="available"
                                    checked={formData.available}
                                    onCheckedChange={(checked) => setFormData({ ...formData, available: checked as boolean })}
                                />
                                <Label htmlFor="available" className="cursor-pointer">
                                    Disponible
                                </Label>
                            </div>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="imageUrl">URL de Imagen *</Label>
                        <Input
                            id="imageUrl"
                            type="url"
                            value={formData.imageUrl}
                            onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="description">Descripción *</Label>
                        <Textarea
                            id="description"
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            required
                            rows={3}
                        />
                    </div>

                    <DialogFooter>
                        <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={loading}>
                            {loading ? "Guardando..." : room ? "Actualizar" : "Crear"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

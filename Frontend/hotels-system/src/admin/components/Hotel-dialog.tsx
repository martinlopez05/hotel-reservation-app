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
import type { Hotel } from "@/Hotels/data/hotel.interface"
import { toast } from "react-toastify"
import { UserContext } from "@/context/UserContext"

type HotelDialogProps = {
    open: boolean
    onOpenChange: (open: boolean) => void
    hotel: Hotel | null
    onSuccess: () => void
}

export function HotelDialog({ open, onOpenChange, hotel, onSuccess }: HotelDialogProps) {

    const BASE_URL_HOTEL = import.meta.env.VITE_API_URL_HOTEL;
    const [loading, setLoading] = useState(false)
    const [formData, setFormData] = useState({
        name: "",
        address: "",
        country: "",
        location: "",
        description: "",
        imageUrl: "",
        rating: 5,
        email: "",
        phone: "",
    })

    const { user } = use(UserContext);

    useEffect(() => {
        if (hotel) {
            setFormData({
                name: hotel.name,
                address: hotel.address,
                country: hotel.country,
                location: hotel.location,
                description: hotel.description,
                imageUrl: hotel.imageUrl,
                rating: hotel.rating,
                email: hotel.email,
                phone: hotel.phone,
            })
        } else {
            setFormData({
                name: "",
                address: "",
                country: "",
                location: "",
                description: "",
                imageUrl: "",
                rating: 5,
                email: "",
                phone: "",
            })
        }
    }, [hotel, open])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setLoading(true)


        try {
            const url = hotel ? `${BASE_URL_HOTEL}/${hotel.id}` : `${BASE_URL_HOTEL}`
            const method = hotel ? "PUT" : "POST"

            const response = await fetch(url, {
                method,
                headers: {
                    "Authorization": `Bearer ${user ? user.token : 0}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            })

            if (response.ok) {
                if (method === 'POST') {
                    toast.success('Hotel creado correctamente')
                }
                if (method === 'PUT') {
                    toast.success('Hotel editado correctamente')
                }
                onSuccess()
            }
        } catch (error) {
            console.error("Error saving hotel:", error)
        } finally {
            setLoading(false)
        }
    }

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>{hotel ? "Editar Hotel" : "Nuevo Hotel"}</DialogTitle>
                    <DialogDescription>
                        {hotel ? "Modifica los datos del hotel" : "Completa los datos del nuevo hotel"}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="name">Nombre *</Label>
                            <Input
                                id="name"
                                value={formData.name}
                                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Email *</Label>
                            <Input
                                id="email"
                                type="email"
                                value={formData.email}
                                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                required
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="address">Dirección *</Label>
                        <Input
                            id="address"
                            value={formData.address}
                            onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                            required
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="country">País *</Label>
                            <Input
                                id="country"
                                value={formData.country}
                                onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="location">Ciudad *</Label>
                            <Input
                                id="location"
                                value={formData.location}
                                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                                required
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="phone">Teléfono *</Label>
                            <Input
                                id="phone"
                                value={formData.phone}
                                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                required
                            />
                        </div>

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
                            {loading ? "Guardando..." : hotel ? "Actualizar" : "Crear"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

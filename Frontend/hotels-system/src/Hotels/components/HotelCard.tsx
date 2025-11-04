import { Button } from '@/components/ui/button'
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from '@/components/ui/card'
import { MapPin, Star } from 'lucide-react'
import { Link } from 'react-router'
import type { Hotel } from '../data/hotel.interface'

interface HotelCardProps {
    hotel: Hotel;
}


const HotelCard = ({ hotel }: HotelCardProps) => {
    return (
        <Card key={hotel.id} className="overflow-hidden hover:shadow-lg transition-shadow">
            <div className="relative h-56 w-full">
                <img
                    src={hotel.imageUrl || "/placeholder.svg"}
                    alt={hotel.name}
                    className="w-full h-full object-cover"
                />
            </div>
            <CardHeader>
                <CardTitle className="text-balance">{hotel.name}</CardTitle>
                <CardDescription className="flex items-center gap-1">
                    <MapPin className="h-4 w-4" />
                    {hotel.location}, {hotel.country}
                </CardDescription>
            </CardHeader>
            <CardContent>
                <p className="text-sm text-muted-foreground mb-4 text-pretty">{hotel.description}</p>
                <div className="flex items-center gap-1 mb-2">
                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    <span className="font-semibold">{hotel.rating}</span>
                    <span className="text-sm text-muted-foreground">/ 5</span>
                </div>
            </CardContent>
            <CardFooter>
                <Link to={`/hotel/${hotel.id}`} className="w-full">
                    <Button className="w-full">Ver Detalles</Button>
                </Link>
            </CardFooter>
        </Card>
    )
}

export default HotelCard

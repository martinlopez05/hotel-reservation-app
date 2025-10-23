import React from 'react'
import HotelCard from './HotelCard'
import type { Hotel } from '../data/hotel.interface'


interface PropsHotelGrid {
    hotels: Hotel[]
}

const HotelGrid = ({ hotels }: PropsHotelGrid) => {
    return (
        <div className="min-h-screen bg-muted/10 py-10">
            <div className="container mx-auto px-6">
                {hotels.length === 0 ? (
                    <div className="text-center py-12">
                        <p className="text-lg text-muted-foreground">
                            No se encontraron hoteles con los criterios seleccionados
                        </p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {hotels.map((hotel: Hotel) => (
                            <HotelCard key={hotel.id} hotel={hotel} />
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}
export default HotelGrid

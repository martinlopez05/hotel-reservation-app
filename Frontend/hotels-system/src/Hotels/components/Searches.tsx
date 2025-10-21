import React, { useState } from 'react'
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { Search } from 'lucide-react'
import { Input } from '@/components/ui/input'
import type { Hotel } from '../types/hotel.interface'

interface PropsSearches {
    hotels: Hotel[]
}

const Searches = ({ hotels }: PropsSearches) => {
    const [searchTerm, setSearchTerm] = useState("")
    const [locationFilter, setLocationFilter] = useState("all")



    const uniqueLocations = Array.from(new Set(hotels.map((h) => h.location)))
    return (
        <section className="container mx-auto px-4 py-8">
            <div className="flex flex-col md:flex-row gap-4 mb-8">
                <div className="flex-1 relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Buscar hoteles..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-10"
                    />
                </div>
                <Select value={locationFilter} onValueChange={setLocationFilter}>
                    <SelectTrigger className="w-full md:w-[200px]">
                        <SelectValue placeholder="Ubicación" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="all">Todas las ubicaciones</SelectItem>
                        {uniqueLocations.map((location) => (
                            <SelectItem key={location} value={location}>
                                {location}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>
        </section>
    )
}

export default Searches

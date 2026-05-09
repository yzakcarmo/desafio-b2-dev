interface Props {
  status: string
}

const config: Record<string, { label: string; className: string }> = {
  PENDING:   { label: 'Pendente',   className: 'bg-yellow-100 text-yellow-800' },
  CONFIRMED: { label: 'Confirmado', className: 'bg-green-100 text-green-800' },
  CANCELLED: { label: 'Cancelado',  className: 'bg-red-100 text-red-800' },
}

export default function StatusBadge({ status }: Props) {
  const { label, className } = config[status] ?? { label: status, className: 'bg-gray-100 text-gray-700' }
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${className}`}>
      {label}
    </span>
  )
}

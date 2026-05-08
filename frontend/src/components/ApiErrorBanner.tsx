import { ApiError } from '../api/client'

interface Props {
  error: ApiError
}

export default function ApiErrorBanner({ error }: Props) {
  return (
    <div className="bg-red-50 border border-red-200 rounded-lg p-4 space-y-1">
      <div className="flex items-center gap-2">
        <span className="text-xs font-mono bg-red-100 text-red-700 px-1.5 py-0.5 rounded">{error.code}</span>
        <span className="text-sm font-medium text-red-800">{error.message}</span>
      </div>
      {error.details.length > 0 && (
        <ul className="mt-2 space-y-0.5 list-disc list-inside">
          {error.details.map((d, i) => (
            <li key={i} className="text-xs text-red-700">{d}</li>
          ))}
        </ul>
      )}
    </div>
  )
}
